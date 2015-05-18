package betsy.bpel.engines.orchestra;

import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class OrchestraInstaller {

    private final Path serverDir = Paths.get("server/orchestra");
    private final Path installDir = serverDir.resolve("orchestra-cxf-tomcat-4.9.0");

    public Path getAntPath() {
        return Configuration.getAntHome().resolve("bin");
    }

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir);

        TomcatInstaller tomcatInstaller = TomcatInstaller.v7(serverDir);
        tomcatInstaller.install();

        String fileName = "orchestra-cxf-tomcat-4.9.0.zip";
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), serverDir);

        setPropertyInPropertiesFile(installDir.resolve("conf").resolve("install.properties"), "catalina.home", "../apache-tomcat-7.0.26");


        DefaultGroovyMethods.println(this, "after properties have been set");
        //ant.ant target: "install", dir: installDir

        // clean up data (with db and config files in the users home directory)
        String cmd = getAntPath().toAbsolutePath().toString() + "/ant install";
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(installDir, cmd));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(installDir, cmd));
    }

    private static void setPropertyInPropertiesFile(Path propertiesFile, String key, String value) {
        Properties properties = new Properties();

        // read
        try (BufferedReader reader = Files.newBufferedReader(propertiesFile)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load property file " + propertiesFile, e);
        }

        // modify
        properties.setProperty(key, value);

        // write
        try (BufferedWriter writer = Files.newBufferedWriter(propertiesFile)) {
            properties.store(writer, null);
        } catch (IOException e) {
            throw new IllegalStateException("Could not store property file " + propertiesFile, e);
        }
    }


}
