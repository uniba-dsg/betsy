package betsy.data.engines.bonita

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.tasks.ConsoleTasks

/**
 * Created with IntelliJ IDEA.
 * User: stavorndran
 * Date: 03.03.14
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
class BonitaEngine extends LocalEngine{

    private static final AntBuilder ant = AntUtil.builder()

    @Override
    String getName() {
        "bonita"
    }

    String getBonitaUrl(){
        "http://localhost:8080"
    }

    @Override
    void deploy(BetsyProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        new BonitaInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("bin"), "startup.bat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("bin/startup.sh")))

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: bonitaUrl
        }
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("bin"), "shutdown.bat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("bin/shutdown.sh")))
    }

    @Override
    boolean isRunning() {
        ant.fail(message: "tomcat for engine ${serverPath} is still running") {
            condition() {
                http url: bonitaUrl
            }
        }
    }
}
