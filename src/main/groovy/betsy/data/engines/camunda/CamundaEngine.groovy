package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.tasks.ConsoleTasks

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

    @Override
    void deploy(BetsyProcess process) {
        new CamundaDeployer(processName: process.name,
                packageFilePath: process.targetPackageFilePath,
                deploymentDirPath: getDeploymentDir(),
                logFilePath: Paths.get("") //TODO
        ).deploy()
    }

    void deployTest(){
        new CamundaDeployer(processName: "loan-approval-0.0.1-SNAPSHOT",
                packageFilePath: Paths.get("downloads/loan-approval-0.0.1-SNAPSHOT.war"),
                deploymentDirPath: Paths.get("server/camunda/server/apache-tomcat-7.0.33/webapps/"),
                logFilePath: Paths.get("") //TODO
        ).deploy()
    }

    @Override
    void buildArchives(BetsyProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
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
