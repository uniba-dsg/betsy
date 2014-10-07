package betsy.bpel.engines.tomcat

import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.URLTasks
import betsy.common.tasks.WaitTasks

import java.nio.file.Path
/**
 * Responsible for starting and stopping tomcat as well as all tomcat related paths and properties.
 */
class Tomcat {

    /**
     * the port of the tomcat
     */
    int port = 8080

    /**
     * The directory in which the tomcat has its directory.
     * Should contain a directory called <code>apache-tomcat-7.0.53</code>
     */
    Path engineDir

    String tomcatName = "apache-tomcat-7.0.53"

    Path getTomcatDir() {
        engineDir.resolve(tomcatName)
    }

    Path getTomcatLogsDir() {
        tomcatDir.resolve("logs")
    }

    Path getTomcatBinDir() {
        tomcatDir.resolve("bin")
    }

    Path getTomcatWebappsDir() {
        tomcatDir.resolve("bin")
    }

    public void deployWar(Path war) {
        FileTasks.copyFileIntoFolder(war, getTomcatWebappsDir())
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

        WaitTasks.waitForAvailabilityOfUrl(30000, 500, getTomcatUrl());

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
    boolean checkIfIsRunning() {
        URLTasks.isUrlAvailable(getTomcatUrl());
    }

}
