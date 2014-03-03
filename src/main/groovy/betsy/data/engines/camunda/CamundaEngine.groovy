package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader

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

    @Override
    void deploy(BetsyProcess process) {
        new CamundaDeployer(processName: "Simple BPM",
                packageFilePath: Paths.get("test/camunda/tasks__simple/war/${process.getName()}.war"),
                deploymentDirPath: Paths.get("server/camunda/server/apache-tomcat-7.0.33/webapps/"),
                logFilePath: Paths.get("server/camunda/server/apache-tomcat-7.0.33/bin/") //TODO
        ).deploy()
    }

    @Override
    void buildArchives(BetsyProcess process) {
        ant.xslt(in: "bpmnRes/files/tasks/simple/process/${process.getName()}.bpmn", out: "${process.getTargetPath()}/war/WEB-INF/classes/${process.getName()}.bpmn", style: xsltPath.resolve("camunda.xsl"))
        new CamundaResourcesGenerator().generateWar("Simple BPM", "org.camunda.bpm.simple", process.name)
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void storeLogs(BetsyProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void install() {
        new CamundaInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "camunda_startup.bat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("camunda_startup.sh")))

        /*ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: camundaUrl
        } */
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
}
