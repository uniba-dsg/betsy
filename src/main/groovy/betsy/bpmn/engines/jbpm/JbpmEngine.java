package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.reporting.BPMNTestcaseMerger;
import betsy.common.config.Configuration;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class JbpmEngine extends BPMNEngine {
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
        final String mvnCommand = String.valueOf(mavenPath.toAbsolutePath()) + "/mvn -q clean install";
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(process.getTargetPath().resolve("project"), mvnCommand));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(process.getTargetPath().resolve("project"), mvnCommand));

        //wait for maven to deploy
        WaitTasks.sleep(1500);

        //preparing ssh
        //delete known_hosts file for do not getting trouble with changing remote finger print
        //FileTasks.deleteFile(Paths.get(homeDir + "/.ssh/known_hosts"))
        FileTasks.createFile(Paths.get(getHomeDir() + "/.ssh/config"), "Host localhost\n    StrictHostKeyChecking no");

        //deploy by creating a deployment unit, which can be started
        final Path jbpmDeployerPath = Configuration.getJbpmDeployerHome();
        final String deployCommand = "java -jar " + Configuration.get("jbpmdeployer.jar") + " "
                + process.getGroupId() + " " + process.getName() + " " + process.getVersion() + " " + getSystemURL();
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmDeployerPath, deployCommand));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmDeployerPath, deployCommand));

        //waiting for the result of the deployment
        WaitTasks.waitForSubstringInFile(20000, 1000, getJbossStandaloneDir().resolve("log").resolve("server.log"), "de.uniba.dsg");
    }

    @Override
    public void buildArchives(final BPMNProcess process) {

        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"), process.getResourcePath().resolve(process.getName() + ".bpmn"), process.getTargetPath().resolve("project/src/main/resources/" + process.getName() + ".bpmn2-temp"));
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

        // TODO only copy log files from tomcat, the other files are files for the test
        FileTasks.copyFilesInFolderIntoOtherFolder(getJbossStandaloneDir().resolve("log"), process.getTargetLogsPath());

        for (BPMNTestCase tc : process.getTestCases()) {
            FileTasks.copyFileIntoFolder(getServerPath().resolve("log" + tc.getNumber() + ".txt"), process.getTargetLogsPath());
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
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), String.valueOf(getAntPath().toAbsolutePath()) + "/ant -q start.demo.noeclipse"), map);
        Map<String, String> map1 = new LinkedHashMap<>(1);
        map1.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), String.valueOf(getAntPath().toAbsolutePath()) + "/ant -q start.demo.noeclipse"), map1);

        //waiting for jbpm-console for deployment and instantiating
        WaitTasks.waitForSubstringInFile(120000, 5000, getJbossStandaloneDir().resolve("log").resolve("server.log"), "JBAS018559: Deployed \"jbpm-console.war\"");
    }

    @Override
    public void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), String.valueOf(getAntPath().toAbsolutePath()) + "/ant -q stop.demo"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), String.valueOf(getAntPath().toAbsolutePath()) + "/ant -q stop.demo"));

        try {
            //waiting for shutdown completion using the boot.log file; e.g. "12:42:36,345 INFO  [org.jboss.as] JBAS015950: JBoss AS 7.1.1.Final "Brontes" stopped in 31957ms"
            WaitTasks.waitForSubstringInFile(60000, 5000, getJbossStandaloneDir().resolve("log").resolve("boot.log"), Charset.forName("ISO-8859-1"), "JBAS015950");

            // clean up data (with db and config files in the users home directory)
            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), String.valueOf(getAntPath().toAbsolutePath()) + "/ant -q clean.demo"));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), String.valueOf(getAntPath().toAbsolutePath()) + "/ant -q clean.demo"));
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
            JbpmTester tester = new JbpmTester();
            tester.setTestCase(testCase);
            tester.setName(process.getName());
            tester.setDeploymentId(process.getGroupId() + ":" + process.getName() + ":" + process.getVersion());
            tester.setBaseUrl(getEndpointUrlAsUrl(process));
            tester.setTestSrc(process.getTargetTestSrcPathWithCase(testCase.getNumber()));
            tester.setReportPath(process.getTargetReportsPathWithCase(testCase.getNumber()));
            tester.setTestBin(process.getTargetTestBinPathWithCase(testCase.getNumber()));
            tester.setLogDir(getServerPath());
            tester.runTest();
        }


        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    private URL getEndpointUrlAsUrl(BPMNProcess process) {
        try {
            return new URL(getEndpointUrl(process));
        } catch (MalformedURLException e) {
            throw new RuntimeException("malformed URL", e);
        }
    }

    public void buildTest(final BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(getName() + "." + process.getGroup());
        builder.setLogDir(getServerPath());
        builder.setProcess(process);
        builder.buildTests();
    }

}
