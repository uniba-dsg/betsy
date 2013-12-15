package betsy.data.engines.tomcat

import ant.tasks.AntUtil
import betsy.tasks.ConsoleTasks

import java.nio.file.Path
/**
 * Responsible for starting and stopping tomcat as well as all tomcat related paths and properties.
 */
class Tomcat {

    final AntBuilder ant = AntUtil.builder()

    /**
     * the port of the tomcat
     */
    int port = 8080

    /**
     * The directory in which the tomcat has its directory.
     * Should contain a directory called <code>apache-tomcat-7.0.26</code>
     */
    Path engineDir

    String tomcatName = "apache-tomcat-7.0.26"

    Path getTomcatDir() {
        engineDir.resolve(tomcatName)
    }

    Path getTomcatLogsDir() {
        tomcatDir.resolve("logs")
    }

    String getTomcatUrl() {
        "http://localhost:$port"
    }

    /**
     * Start tomcat and wait until it responds to the <code>tomcatUrl</code>
     */
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(engineDir, "tomcat_startup.bat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(engineDir.resolve("tomcat_startup.sh")))

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: tomcatUrl
        }

    }

    /**
     * Shutdown the tomcat if running.
     */
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq Tomcat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(engineDir.resolve("tomcat_shutdown.sh")))
    }

    /**
     * Throw exception if the tomcat is still running.
     */
    void checkIfIsRunning() {
        ant.fail(message: "tomcat for engine ${engineDir} is still running") {
            condition() {
                http url: tomcatUrl
            }
        }
    }

}
