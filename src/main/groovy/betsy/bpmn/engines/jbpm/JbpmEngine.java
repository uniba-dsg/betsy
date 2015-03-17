package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.reporting.BPMNTestcaseMerger;
import betsy.common.config.Configuration;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class JbpmEngine extends AbstractBPMNEngine {

    private static final Logger LOGGER = Logger.getLogger(JbpmEngine.class);

    @Override
    public String getName() {
        return "jbpm";
    }

    public String getJbpmnUrl() {
        return "http://localhost:8080/jbpm-console";
    }

    public String getSystemURL() {
        return "ssh://admin@localhost:8001/system";
    }

    public String getJbossName() {
        return "jboss-as-7.1.1.Final";
    }

    public Path getJbossStandaloneDir() {
        return getServerPath().resolve(getJbossName()).resolve("standalone");
    }

    public String getHomeDir() {
        String homeDir = System.getenv("HOME");
        if (homeDir == null) {
            homeDir = System.getProperty("user.home");
        }

        return homeDir;
    }

    public Path getAntPath() {
        return Configuration.getAntHome().resolve("bin");
    }

    @Override
    public void deploy(final BPMNProcess process) {
        // maven deployment for pushing it to the local maven repository (jbpm-console will fetch it from there)
        final Path mavenPath = Configuration.getMavenHome().resolve("bin");
        final String mvnCommand = mavenPath.toAbsolutePath() + "/mvn -q clean install";
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(process.getTargetPath().resolve("project"), mvnCommand));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(process.getTargetPath().resolve("project"), mvnCommand));

        //wait for maven to deploy
        WaitTasks.sleep(1500);

        new JbpmDeployer(getJbpmnUrl(), getDeploymentId(process)).deploy();

        //waiting for the result of the deployment
        WaitTasks.waitForSubstringInFile(30000, 1000, getJbossLogDir().resolve("server.log"), getDeploymentId(process));

        // And a few more seconds to ensure availability
        WaitTasks.sleep(5000);
    }

    @Override
    public void buildArchives(final BPMNProcess process) {

        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"), process.getProcess(), process.getTargetPath().resolve("project/src/main/resources/" + process.getName() + ".bpmn2-temp"));
        XSLTTasks.transform(getXsltPath().resolve("jbpm.xsl"), process.getTargetPath().resolve("project/src/main/resources/" + process.getName() + ".bpmn2-temp"), process.getTargetPath().resolve("project/src/main/resources/" + process.getName() + ".bpmn2"));
        FileTasks.deleteFile(process.getTargetPath().resolve("war/WEB-INF/classes/" + process.getName() + ".bpmn2-temp"));

        JbpmResourcesGenerator generator = new JbpmResourcesGenerator();

        generator.setJbpmSrcDir(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/jbpm"));
        generator.setDestDir(process.getTargetPath().resolve("project"));
        generator.setProcessName(process.getName());
        generator.setGroupId(process.getGroupId());
        generator.setVersion(process.getVersion());
        generator.generateProject();
    }

    @Override
    public String getEndpointUrl(BPMNProcess process) {
        return "http://localhost:8080/jbpm-console/";
    }

    @Override
    public void storeLogs(BPMNProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());
        FileTasks.copyFilesInFolderIntoOtherFolder(getJbossLogDir(), process.getTargetLogsPath());

        for (BPMNTestCase tc : process.getTestCases()) {
            FileTasks.copyFileIntoFolder(getServerPath().resolve("log" + tc.getNumber() + ".txt"), process.getTargetLogsPath());
            if (tc.getAssertions().contains(BPMNAssertions.EXECUTION_PARALLEL.toString())) {
                // Copy parallel logs from Tomcat bin to TargetLogsPath
                Path parallelLogOne = getServerPath().resolve("log" + tc.getNumber() + "_parallelOne.txt");
                FileTasks.copyFileIntoFolder(parallelLogOne, process.getTargetLogsPath());

                Path parallelLogTwo = getServerPath().resolve("log" + tc.getNumber() + "_parallelTwo.txt");
                FileTasks.copyFileIntoFolder(parallelLogTwo, process.getTargetLogsPath());
            }
        }
    }

    @Override
    public void install() {
        JbpmInstaller installer = new JbpmInstaller();
        installer.setDestinationDir(getServerPath());
        installer.install();
    }

    @Override
    public void startup() {
        Path pathToJava7 = Configuration.getJava7Home();
        FileTasks.assertDirectory(pathToJava7);

        Map<String, String> map = new LinkedHashMap<>(1);
        map.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), getAntPath().toAbsolutePath() + "/ant -q start.demo.noeclipse"), map);
        Map<String, String> map1 = new LinkedHashMap<>(1);
        map1.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), getAntPath().toAbsolutePath() + "/ant -q start.demo.noeclipse"), map1);

        //waiting for jbpm-console for deployment and instantiating
        WaitTasks.waitForSubstringInFile(180000, 5000, getJbossLogDir().resolve("server.log"), "JBAS018559: Deployed \"jbpm-console.war\"");
    }

    private Path getJbossLogDir() {
        return getJbossStandaloneDir().resolve("log");
    }

    @Override
    public void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), getAntPath().toAbsolutePath() + "/ant -q stop.demo"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), getAntPath().toAbsolutePath() + "/ant -q stop.demo"));

        if(FileTasks.hasNoFile(getJbossLogDir().resolve("boot.log"))) {
            LOGGER.info("Could not shutdown, because boot.log does not exist. this indicates that the engine was never started");
            return;
        }

        try {
            //waiting for shutdown completion using the boot.log file; e.g. "12:42:36,345 INFO  [org.jboss.as] JBAS015950: JBoss AS 7.1.1.Final "Brontes" stopped in 31957ms"
            WaitTasks.waitForSubstringInFile(120000, 5000, getJbossLogDir().resolve("boot.log"), "JBAS015950");

            // clean up data (with db and config files in the users home directory)
            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), getAntPath().toAbsolutePath() + "/ant -q clean.demo"));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), getAntPath().toAbsolutePath() + "/ant -q clean.demo"));
        } catch (IllegalStateException ex) {
            //swallow
        }
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(getJbpmnUrl());
    }

    public void testProcess(final BPMNProcess process) {
        for (BPMNTestCase testCase : process.getTestCases()) {
            BPMNTester bpmnTester = new BPMNTester();
            bpmnTester.setSource(process.getTargetTestSrcPathWithCase(testCase.getNumber()));
            bpmnTester.setTarget(process.getTargetTestBinPathWithCase(testCase.getNumber()));
            bpmnTester.setReportPath(process.getTargetReportsPathWithCase(testCase.getNumber()));

            JbpmTester tester = new JbpmTester();
            tester.setTestCase(testCase);
            tester.setName(process.getName());
            tester.setDeploymentId(getDeploymentId(process));
            tester.setProcessStartUrl(getJbpmnUrl() + "/rest/runtime/" + tester.getDeploymentId() + "/process/" + process.getName() + "/start");
            tester.setProcessHistoryUrl(getJbpmnUrl() + "/rest/runtime/" + tester.getDeploymentId() + "/history/instance/1");
            tester.setBpmnTester(bpmnTester);
            tester.setLogDir(getServerPath());
            tester.setServerLogFile(getJbossLogDir().resolve("server.log"));
            tester.runTest();
        }

        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    private static String getDeploymentId(BPMNProcess process) {
        return process.getGroupId() + ":" + process.getName() + ":" + process.getVersion();
    }

    public void buildTest(final BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(getName() + "." + process.getGroup());
        builder.setLogDir(getServerPath());
        builder.setProcess(process);
        builder.buildTests();
    }

}
