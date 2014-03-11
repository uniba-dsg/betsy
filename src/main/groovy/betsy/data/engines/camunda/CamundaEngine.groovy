package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
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
class CamundaEngine extends LocalEngine {

    private static final AntBuilder ant = AntUtil.builder()

    @Override
    String getName() {
        "camunda"
    }

    String getCamundaUrl(){
        "http://localhost:8080"
    }

    String getRestURL(){
        "http://localhost:8080/engine-rest/engine/default"
    }

    String getTomcatName(){
        "apache-tomcat-7.0.33"
    }

    Path getTomcatDir(){
        serverPath.resolve("server/${tomcatName}")
    }

    @Override
    void deploy(BetsyProcess process) {
        new CamundaDeployer(processName: process.name,
                packageFilePath: Paths.get("${process.targetPath}/war/${process.name}.war"),
                deploymentDirPath: tomcatDir.resolve("webapps")
        ).deploy()
    }

    @Override
    void buildArchives(BetsyProcess process) {
        Path resourceDir = Paths.get("bpmnRes/files/${process.group}/${process.name}")
        ant.xslt(in: resourceDir.resolve("process/${process.name}.bpmn"),
                out: process.targetPath.resolve("war/WEB-INF/classes/${process.name}.bpmn"),
                style: xsltPath.resolve("camunda.xsl"))
        new CamundaResourcesGenerator(groupId: "org.camunda.bpm.dsg",
                processName: process.name,
                srcDir: resourceDir,
                destDir: process.targetPath.resolve("war"),
                version: "0.0.1-SNAPSHOT").generateWar()
    }

    void buildTest(BetsyProcess process){
        List<String> assertionList = new ArrayList<String>()
        assertionList.add("success")
        new BPMNTestBuilder(packageString: "${name}.${process.group}.${process.name}",
                logFile: tomcatDir.resolve("bin/log.txt"),
                unitTestDir: process.targetPath.resolve("testSrc"),
                assertionList: assertionList).buildTest()
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void storeLogs(BetsyProcess process) {
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

    void testProcess(BetsyProcess process){
        new CamundaTester(testSrc: process.targetPath.resolve("testSrc"),
                restURL: restURL,
                reportPath: process.targetReportsPath,
                testBin: process.targetPath.resolve("testBin"),
                key: "SimpleApplication",
                serverDir: tomcatDir).runTest()
    }
}
