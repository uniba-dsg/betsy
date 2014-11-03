package betsy.bpel.engines.tomcat;

import betsy.common.tasks.*;

import java.nio.file.Path;

/**
 * Responsible for starting and stopping tomcat as well as all tomcat related paths and properties.
 */
public class Tomcat {
    public Path getTomcatDir() {
        return engineDir.resolve(tomcatName);
    }

    public Path getTomcatLogsDir() {
        return getTomcatDir().resolve("logs");
    }

    public Path getTomcatBinDir() {
        return getTomcatDir().resolve("bin");
    }

    public Path getTomcatWebappsDir() {
        return getTomcatDir().resolve("webapps");
    }

    public void deployWar(Path war) {
        FileTasks.copyFileIntoFolder(war, getTomcatWebappsDir());
        String warFileNameWithoutExtension = FileTasks.getFilenameWithoutExtension(war);
        ZipTasks.unzip(getTomcatWebappsDir().resolve(war.getFileName()), getTomcatWebappsDir().resolve(warFileNameWithoutExtension));
    }

    public String getTomcatUrl() {
        return "http://localhost:" + port;
    }

    /**
     * Start tomcat and wait until it responds to the <code>tomcatUrl</code>
     */
    public void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(engineDir, "tomcat_startup.bat"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(engineDir.resolve("tomcat_startup.sh")));

        WaitTasks.waitForAvailabilityOfUrl(30000, 500, getTomcatUrl());

    }

    /**
     * Shutdown the tomcat if running.
     */
    public void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq Tomcat"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(engineDir.resolve("tomcat_shutdown.sh")));
    }

    /**
     * Throw exception if the tomcat is still running.
     */
    public boolean checkIfIsRunning() {
        return URLTasks.isUrlAvailable(getTomcatUrl());
    }

    public void addLib(Path path) {
        FileTasks.copyFileIntoFolder(path, getTomcatDir().resolve("lib"));
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Path getEngineDir() {
        return engineDir;
    }

    public void setEngineDir(Path engineDir) {
        this.engineDir = engineDir;
    }

    public String getTomcatName() {
        return tomcatName;
    }

    public void setTomcatName(String tomcatName) {
        this.tomcatName = tomcatName;
    }

    /**
     * the port of the tomcat
     */
    private int port = 8080;
    /**
     * The directory in which the tomcat has its directory.
     * Should contain a directory called <code>apache-tomcat-7.0.53</code>
     */
    private Path engineDir;
    private String tomcatName = "apache-tomcat-7.0.53";
}
