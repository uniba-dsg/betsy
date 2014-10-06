package betsy.bpmn.engines.camunda

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase
import betsy.bpmn.engines.BPMNEngine
import betsy.bpmn.model.BPMNTestBuilder
import betsy.bpmn.reporting.BPMNTestcaseMerger
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import betsy.tasks.WaitTasks

import java.nio.file.Path
import java.nio.file.Paths;

class CamundaEngine extends BPMNEngine {

    @Override
    String getName() {
        "camunda"
    }

    String getCamundaUrl(){
        "http://localhost:8080"
    }

    String getTomcatName(){
        "apache-tomcat-7.0.33"
    }

    Path getTomcatDir(){
        serverPath.resolve("server").resolve(tomcatName)
    }

    @Override
    void deploy(BPMNProcess process) {
        new CamundaDeployer(processName: process.name,
                packageFilePath: Paths.get("${process.targetPath}/war/${process.name}.war"),
                deploymentDirPath: tomcatDir.resolve("webapps")
        ).deploy()

        //wait until it is deployed
        WaitTasks.sleep(15000)
    }

    @Override
    void buildArchives(BPMNProcess process) {
        ant.xslt(in: process.resourcePath.resolve("${process.name}.bpmn"),
                out: process.targetPath.resolve("war/WEB-INF/classes/${process.name}.bpmn"),
                style: xsltPath.resolve("camunda.xsl"))
        new CamundaResourcesGenerator(groupId: process.groupId,
                processName: process.name,
                srcDir: process.resourcePath,
                destDir: process.targetPath.resolve("war"),
                version: process.version).generateWar()
    }

    @Override
    void buildTest(BPMNProcess process){
        new BPMNTestBuilder(packageString: "${name}.${process.group}",
                logDir: tomcatDir.resolve("bin"),
                process: process
        ).buildTests()
    }

    @Override
    String getEndpointUrl(BPMNProcess process) {
        "http://localhost:8080/engine-rest/engine/default"
    }

    @Override
    void storeLogs(BPMNProcess process) {
        FileTasks.mkdirs(process.targetLogsPath)
        ant.copy(todir: process.targetLogsPath) {
            ant.fileset(dir: tomcatDir.resolve("logs"))
            for(BPMNTestCase tc: process.testCases){
                ant.fileset(file: tomcatDir.resolve("bin").resolve("log${tc.number}.txt"))
            }
        }
    }

    @Override
    void install() {
        new CamundaInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "camunda_startup.bat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("camunda_startup.sh")))

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: camundaUrl
        }
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq Tomcat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("camunda_shutdown.sh")))
    }

    @Override
    boolean isRunning() {
        try{
            ant.fail(message: "tomcat for engine ${serverPath} is still running") {
                condition() {
                    http url: camundaUrl
                }
            }
            return false
        } catch (Exception ignore) {
            return true
        }
    }

    @Override
    void testProcess(BPMNProcess process){
        for (BPMNTestCase testCase : process.testCases){
            new CamundaTester(testCase: testCase,
                    testSrc: process.getTargetTestSrcPathWithCase(testCase.number),
                    restURL: getEndpointUrl(process),
                    reportPath: process.getTargetReportsPathWithCase(testCase.number),
                    testBin: process.getTargetTestBinPathWithCase(testCase.number),
                    key: process.name,
                    logDir: tomcatDir.resolve("logs")).runTest()
        }
        new BPMNTestcaseMerger(reportPath: process.targetReportsPath).mergeTestCases()
    }
}
