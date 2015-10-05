package betsy.common.engines.tomcat;

import betsy.common.config.Configuration;
import betsy.common.tasks.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Responsible for starting and stopping tomcat as well as all tomcat related paths and properties.
 */
public class Tomcat {

    public enum JavaVersion {
        V7, V8;
    }

    /*
     * the port of the tomcat
     */
    private final int port;

    /**
     * The directory in which the tomcat has its directory.
     * Should contain a directory called <code>apache-tomcat-XX.XX.XX</code>
     */
    private final Path parentFolder;

    private final String tomcatName;

    public JavaVersion getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(JavaVersion javaVersion) {
        this.javaVersion = javaVersion;
    }

    private JavaVersion javaVersion = JavaVersion.V8;

    public Tomcat(Path parentFolder, String tomcatName, int port) {
        //FileTasks.assertDirectory(parentFolder);

        this.parentFolder = Objects.requireNonNull(parentFolder);
        this.tomcatName = Objects.requireNonNull(tomcatName);
        this.port = port;
    }

    public static Tomcat v7(Path parentFolder) {
        return new Tomcat(parentFolder, "apache-tomcat-7.0.53", 8080);
    }

    public static Tomcat v5(Path parentFolder) {
        return new Tomcat(parentFolder, "apache-tomcat-5.5.36", 8080);
    }

    public Path getTomcatDir() {
        return parentFolder.resolve(tomcatName);
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
        Map<String, String> environment = getEnvironment();

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(parentFolder, "tomcat_startup.bat"), environment);
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(parentFolder.resolve("tomcat_startup.sh")), environment);

        WaitTasks.waitForAvailabilityOfUrl(30000, 500, getTomcatUrl());

    }

    private Map<String, String> getEnvironment() {
        if(javaVersion == JavaVersion.V8) {
            return Collections.emptyMap();
        } else {
            Path pathToJava7 = Configuration.getJava7Home();
            Map<String, String> environment = new HashMap<>();
            environment.put("JAVA_HOME", pathToJava7.toString());
            return environment;
        }
    }

    /**
     * Shutdown the tomcat if running.
     */
    public void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(parentFolder.resolve("tomcat_shutdown.bat")));
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq " + getTomcatName() + "*"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(parentFolder.resolve("tomcat_shutdown.sh")));
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

    public Path getParentFolder() {
        return parentFolder;
    }

    public String getTomcatName() {
        return tomcatName;
    }

}
