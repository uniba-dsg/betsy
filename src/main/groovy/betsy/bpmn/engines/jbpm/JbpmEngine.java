package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.reporting.BPMNTestcaseMerger;
import betsy.common.config.Configuration;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JbpmEngine extends AbstractBPMNEngine {

    private static final Logger LOGGER = Logger.getLogger(JbpmEngine.class);

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/jbpm");
    }

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "jbpm", "6.0.1");
    }

    public Path getJbpmInstallerPath() {
        return getServerPath().resolve("jbpm-installer");
    }

    public String getJbpmnUrl() {
        return "http://localhost:8080/jbpm-console";
    }

    public String getJbossName() {
        return "jboss-as-7.1.1.Final";
    }

    public String getLogFileNameForShutdownAnalysis() {
        return "boot.log";
    }

    public Path getJbossStandaloneDir() {
        return getJbpmInstallerPath().resolve(getJbossName()).resolve("standalone");
    }

    public Path getAntPath() {
        return Configuration.getAntHome().resolve("bin");
    }

    @Override
    public void deploy(final BPMNProcess process) {
        // maven deployment for pushing it to the local maven repository (jbpm-console will fetch it from there)
        final Path mvnPath = Configuration.getMavenHome().resolve("bin");

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(process.getTargetPath().resolve("project"), mvnPath.toAbsolutePath() + "/mvn -q clean install"));
        ConsoleTasks.setupMvn(mvnPath);
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(process.getTargetPath().resolve("project"), mvnPath.toAbsolutePath() + "/mvn").values("-q", "clean", "install"));

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

        for (Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getJbossLogDir()));
        result.addAll(FileTasks.findAllInFolder(getJbpmInstallerPath(), "log*.txt"));

        return result;
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

        ConsoleTasks.setupAnt(getAntPath());

        Map<String, String> map = new LinkedHashMap<>(1);
        map.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant -q start.demo.noeclipse"), map);
        Map<String, String> map1 = new LinkedHashMap<>(1);
        map1.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant").values("-q", "start.demo.noeclipse"), map1);

        //waiting for jbpm-console for deployment and instantiating
        WaitTasks.waitForSubstringInFile(240000, 5000, getJbossLogDir().resolve("server.log"), "JBAS018559: Deployed \"jbpm-console.war\"");
    }

    private Path getJbossLogDir() {
        return getJbossStandaloneDir().resolve("log");
    }

    @Override
    public void shutdown() {
        ConsoleTasks.setupAnt(getAntPath());

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant -q stop.demo"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant").values("-q", "stop.demo"));

        if (FileTasks.hasNoFile(getJbossLogDir().resolve(getLogFileNameForShutdownAnalysis()))) {
            LOGGER.info("Could not shutdown, because " + getLogFileNameForShutdownAnalysis() + " does not exist. this indicates that the engine was never started");
            return;
        }

        try {
            //waiting for shutdown completion using log files; e.g. "12:42:36,345 INFO  [org.jboss.as] JBAS015950: JBoss AS 7.1.1.Final "Brontes" stopped in 31957ms"
            WaitTasks.waitForSubstringInFile(240000, 5000, getJbossLogDir().resolve(getLogFileNameForShutdownAnalysis()), "JBAS015950");

            // clean up data (with db and config files in the users home directory)
            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant -q clean.demo"));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant").values("-q").values("clean.demo"));
        } catch (IllegalStateException ex) {
            //swallow
        }
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(getJbpmnUrl());
    }

    @Override
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
            tester.setProcessHistoryUrl(createProcessHistoryURL(tester.getDeploymentId()));
            tester.setBpmnTester(bpmnTester);
            tester.setLogDir(getJbpmInstallerPath());
            tester.setServerLogFile(getJbossLogDir().resolve("server.log"));
            tester.runTest();
        }

        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    private static String getDeploymentId(BPMNProcess process) {
        return process.getGroupId() + ":" + process.getName() + ":" + process.getVersion();
    }

    @Override
    public void buildTest(final BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(process.getEngine() + "." + process.getGroup().getName() + "." + getName());
        builder.setLogDir(getJbpmInstallerPath());
        builder.setProcess(process);
        builder.buildTests();
    }

    protected String createProcessHistoryURL(String deploymentId) {
        return getJbpmnUrl() + "/rest/runtime/" + deploymentId + "/history/instance/1";
    }

}
