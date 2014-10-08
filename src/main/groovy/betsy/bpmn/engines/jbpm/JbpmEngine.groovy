package betsy.bpmn.engines.jbpm

import betsy.bpmn.engines.BPMNEngine
import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestBuilder
import betsy.bpmn.model.BPMNTestCase
import betsy.bpmn.reporting.BPMNTestcaseMerger
import betsy.common.config.Configuration
import betsy.common.tasks.*

import java.nio.file.Path
import java.nio.file.Paths

class JbpmEngine extends BPMNEngine {

    @Override
    String getName() {
        "jbpm"
    }

    String getJbpmnUrl() {
        "http://localhost:8080"
    }

    String getSystemURL() {
        "ssh://admin@localhost:8001/system"
    }

    String getJbossName() {
        "jboss-as-7.1.1.Final"
    }

    Path getJbossStandaloneDir() {
        serverPath.resolve(jbossName).resolve("standalone")
    }

    String getHomeDir() {
        String homeDir = System.getenv("HOME")
        if (homeDir == null) {
            homeDir = System.getProperty("user.home")
        }
        return homeDir
    }

    Path getAntPath() {
        Configuration.getAntHome().resolve("bin");
    }

    @Override
    void deploy(BPMNProcess process) {
        // maven deployment for pushing it to the local maven repository (jbpm-console will fetch it from there)
        Path mavenPath = Paths.get("maven/apache-maven-3.2.1/bin")
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(process.targetPath.resolve("project"), "${mavenPath.toAbsolutePath()}/mvn -q clean install"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(process.targetPath.resolve("project"), "${mavenPath.toAbsolutePath()}/mvn -q clean install"))

        //wait for maven to deploy
        WaitTasks.sleep(1500)

        //preparing ssh
        //delete known_hosts file for do not getting trouble with changing remote finger print
        //FileTasks.deleteFile(Paths.get(homeDir + "/.ssh/known_hosts"))
        FileTasks.createFile(Paths.get(homeDir + "/.ssh/config"), """Host localhost
    StrictHostKeyChecking no""")

        //deploy by creating a deployment unit, which can be started
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("jbpmdeployer/JPBM-Deployer-1.1"), "java -jar Jbpm-deployer-1.1.jar ${process.groupId} ${process.name} ${process.version} ${systemURL}"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("jbpmdeployer/JPBM-Deployer-1.1"), "java -jar Jbpm-deployer-1.1.jar ${process.groupId} ${process.name} ${process.version} ${systemURL}"))

        //waiting for the result of the deployment
        WaitTasks.sleep(3000)
    }

    @Override
    void buildArchives(BPMNProcess process) {

        XSLTTasks.transform(xsltPath.resolve("../scriptTask.xsl"),
                process.resourcePath.resolve("${process.name}.bpmn"),
                process.targetPath.resolve("project/src/main/resources/${process.name}.bpmn2-temp"))
        XSLTTasks.transform(xsltPath.resolve("jbpm.xsl"),
                process.targetPath.resolve("project/src/main/resources/${process.name}.bpmn2-temp"),
                process.targetPath.resolve("project/src/main/resources/${process.name}.bpmn2"))
        FileTasks.deleteFile(process.targetPath.resolve("war/WEB-INF/classes/${process.name}.bpmn2-temp"))

        new JbpmResourcesGenerator(
                jbpmSrcDir: Paths.get("src/main/tests/files/bpmnRes/jbpm"),
                destDir: process.targetPath.resolve("project"),
                processName: process.name,
                groupId: process.groupId,
                version: process.version
        ).generateProject()
    }

    @Override
    String getEndpointUrl(BPMNProcess process) {
        "http://localhost:8080/jbpm-console/"
    }

    @Override
    void storeLogs(BPMNProcess process) {
        FileTasks.mkdirs(process.targetLogsPath)

        // TODO only copy log files from tomcat, the other files are files for the test
        FileTasks.copyFilesInFolderIntoOtherFolder(jbossStandaloneDir.resolve("log"), process.getTargetLogsPath());

        for (BPMNTestCase tc : process.getTestCases()) {
            FileTasks.copyFileIntoFolder(serverPath.resolve("log" + tc.getNumber() + ".txt"), process.getTargetLogsPath());
        }
    }

    @Override
    void install() {
        new JbpmInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        Path pathToJava7 = Configuration.getJava7Home();
        FileTasks.assertDirectory(pathToJava7)

        ConsoleTasks.executeOnWindowsAndIgnoreError(
                ConsoleTasks.CliCommand.build(serverPath, "${antPath.toAbsolutePath()}/ant -q start.demo.noeclipse"),
                ["JAVA_HOME": pathToJava7.toString()])
        ConsoleTasks.executeOnUnixAndIgnoreError(
                ConsoleTasks.CliCommand.build(serverPath, "${antPath.toAbsolutePath()}/ant -q start.demo.noeclipse"),
                ["JAVA_HOME": pathToJava7.toString()])

        WaitTasks.waitForAvailabilityOfUrl(30_000, 500, getJbpmnUrl());

        //waiting for jbpm-console for deployment and instantiating
        WaitTasks.sleep(120000)
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "${antPath.toAbsolutePath()}/ant -q stop.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "${antPath.toAbsolutePath()}/ant -q stop.demo"))
        //waiting for shutdown
        WaitTasks.sleep(5000)
        // clean up data (with db and config files in the users home directory)
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "${antPath.toAbsolutePath()}/ant -q clean.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "${antPath.toAbsolutePath()}/ant -q clean.demo"))
    }

    @Override
    boolean isRunning() {
        URLTasks.isUrlAvailable(getJbpmnUrl());
    }

    void testProcess(BPMNProcess process) {
        for (BPMNTestCase testCase : process.testCases) {
            new JbpmTester(testCase: testCase,
                    name: process.name,
                    deploymentId: "${process.groupId}:${process.name}:${process.version}",
                    baseUrl: new URL(getEndpointUrl(process)),
                    testSrc: process.getTargetTestSrcPathWithCase(testCase.number),
                    reportPath: process.getTargetReportsPathWithCase(testCase.number),
                    testBin: process.getTargetTestBinPathWithCase(testCase.number),
                    logDir: serverPath
            ).runTest()
        }

        BPMNTestcaseMerger merger = new BPMNTestcaseMerger();
        merger.setReportPath(process.getTargetReportsPath());
        merger.mergeTestCases();
    }

    void buildTest(BPMNProcess process) {
        new BPMNTestBuilder(packageString: "${name}.${process.group}",
                logDir: serverPath,
                process: process
        ).buildTests()
    }
}
