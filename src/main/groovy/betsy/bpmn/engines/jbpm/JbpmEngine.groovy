package betsy.bpmn.engines.jbpm

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase
import betsy.bpmn.engines.BPMNEngine
import betsy.bpmn.model.BPMNTestBuilder
import betsy.bpmn.reporting.BPMNTestcaseMerger
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks

import java.nio.file.Path
import java.nio.file.Paths

class JbpmEngine extends BPMNEngine {
    @Override
    String getName() {
        "jbpm"
    }

    String getJbpmnUrl(){
        "http://localhost:8080"
    }

    String getSystemURL(){
        "ssh://admin@localhost:8001/system"
    }

    String getJbossName(){
        "jboss-as-7.1.1.Final"
    }

    Path getJbossStandaloneDir(){
        serverPath.resolve(jbossName).resolve("standalone")
    }

    String getHomeDir(){
        String homeDir = System.getenv("HOME")
        if(homeDir == null){
            homeDir = System.getProperty("user.home")
        }
        return homeDir
    }

    @Override
    void deploy(BPMNProcess process) {
        // maven deployment for pushing it to the local maven repository (jbpm-console will fetch it from there)
        Path mavenPath = Paths.get("maven/apache-maven-3.2.1/bin")
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(process.targetPath.resolve("project"), "${mavenPath.toAbsolutePath()}/mvn -q clean install"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(process.targetPath.resolve("project"), "${mavenPath.toAbsolutePath()}/mvn -q clean install"))
        //wait for maven to deploy
        Thread.sleep(1500)

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
        ant.xslt(in: process.resourcePath.resolve("${process.name}.bpmn"),
                out: process.targetPath.resolve("project/src/main/resources/${process.name}.bpmn2-temp"),
                style: xsltPath.resolve("../scriptTask.xsl"))
        ant.xslt(in: process.resourcePath.resolve("${process.name}.bpmn-temp"),
                out: process.targetPath.resolve("project/src/main/resources/${process.name}.bpmn2"),
                style: xsltPath.resolve("jbpm.xsl"))
        ant.delete(file: process.targetPath.resolve("war/WEB-INF/classes/${process.name}.bpmn-temp"))
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
        ant.copy(todir: process.targetLogsPath) {
            ant.fileset(dir: jbossStandaloneDir.resolve("log"))
            for(BPMNTestCase tc: process.testCases){
                ant.fileset(file: serverPath.resolve("log${tc.number}.txt"))
            }
        }
    }

    @Override
    void install() {
        new JbpmInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant -q start.demo.noeclipse"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant -q start.demo.noeclipse"))

        //waiting for jboss to startup
        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: jbpmnUrl
        }

        //waiting for jbpm-console for deployment and instantiating
        WaitTasks.sleep(120000)
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant -q stop.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant -q stop.demo"))
        //waiting for shutdown
        WaitTasks.sleep(5000)
        // clean up data (with db and config files in the users home directory)
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant -q clean.demo"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "ant -q clean.demo"))
    }

    @Override
    boolean isRunning() {
        try{
            ant.fail(message: "JBoss for engine ${serverPath} is still running") {
                condition() {
                    http url: jbpmnUrl
                }
            }
            return false
        } catch (Exception ignore) {
            return true
        }
    }

    void testProcess(BPMNProcess process){
        for (BPMNTestCase testCase : process.testCases){
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
        new BPMNTestcaseMerger(reportPath: process.targetReportsPath).mergeTestCases()
    }

    void buildTest(BPMNProcess process){
        new BPMNTestBuilder(packageString: "${name}.${process.group}",
                logDir: serverPath,
                process: process
        ).buildTests()
    }
}
