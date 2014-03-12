package betsy.data.engines.camunda

import betsy.data.BPMNProcess
import betsy.data.engines.BPMNEngine
import betsy.executables.BPMNTestBuilder
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
        ant.xslt(in: process.resourcePath.resolve("process/${process.name}.bpmn"),
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
        List<String> assertionList = new ArrayList<String>()
        assertionList.add("success")
        assertionList.add("true")
        new BPMNTestBuilder(packageString: "${name}.${process.group}.${process.name}",
                logFile: tomcatDir.resolve("bin").resolve("log.txt"),
                unitTestDir: process.targetTestSrcPath,
                assertionList: assertionList).buildTest()
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
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("camunda_shutdown.bat")))
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
        new CamundaTester(testSrc: process.targetTestSrcPath,
                restURL: getEndpointUrl(process),
                reportPath: process.targetReportsPath,
                testBin: process.targetTestBinPath,
                key: process.key,
                serverDir: tomcatDir).runTest()
    }
}
