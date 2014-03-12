package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BetsyProcess
import betsy.data.engines.BPMNLocalEngine
import betsy.data.engines.LocalEngine
import betsy.executables.BPMNTestBuilder
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader

import java.nio.file.Path
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 09:58
 */
class CamundaEngine extends BPMNLocalEngine {

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
        serverPath.resolve("server/${tomcatName}")
    }

    @Override
    void deploy(BPMNProcess process) {
        new CamundaDeployer(processName: process.name,
                packageFilePath: Paths.get("${process.targetPath}/war/${process.name}.war"),
                deploymentDirPath: tomcatDir.resolve("webapps")
        ).deploy()
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

    void buildTest(BPMNProcess process){
        List<String> assertionList = new ArrayList<String>()
        assertionList.add("success")
        new BPMNTestBuilder(packageString: "${name}.${process.group}.${process.name}",
                logFile: tomcatDir.resolve("bin/log.txt"),
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
        ant.fail(message: "tomcat for engine ${serverPath} is still running") {
            condition() {
                http url: camundaUrl
            }
        }
    }

    void testProcess(BPMNProcess process){
        new CamundaTester(testSrc: process.targetTestSrcPath,
                restURL: getEndpointUrl(process),
                reportPath: process.targetReportsPath,
                testBin: process.targetTestBinPath,
                key: process.key,
                serverDir: tomcatDir).runTest()
    }
}
