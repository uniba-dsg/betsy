package betsy.bpmn.engines.camunda;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;

public class CamundaInstaller {
    public void install() {
        FileTasks.deleteDirectory(destinationDir);
        FileTasks.mkdirs(destinationDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        NetworkTasks.downloadFileFromBetsyRepo(groovyFile);
        FileTasks.copyFileIntoFolder(Configuration.getDownloadsDir().resolve(groovyFile), getTomcatDestinationDir().resolve("lib"));

        FileTasks.createFile(destinationDir.resolve("camunda_startup.bat"), "cd " + String.valueOf(getTomcatBinFolder().toAbsolutePath()) + " && call startup.bat");
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.bat"), "cd " + String.valueOf(getTomcatBinFolder().toAbsolutePath()) + " && call shutdown.bat");

        FileTasks.createFile(destinationDir.resolve("camunda_startup.sh"), "cd " + String.valueOf(getTomcatBinFolder().toAbsolutePath()) + " && ./startup.sh");
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.sh"), "cd " + String.valueOf(getTomcatBinFolder().toAbsolutePath()) + " && ./shutdown.sh");

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", destinationDir.toAbsolutePath().toString()));
    }

    public Path getTomcatDestinationDir() {
        return destinationDir.resolve("server").resolve(tomcatName);
    }

    public Path getTomcatBinFolder() {
        return getTomcatDestinationDir().resolve("bin");
    }

    @Override
    public String toString() {
        return "CamundaInstaller{" + "destinationDir=" + destinationDir + ", fileName='" + fileName + "\'" + ", groovyFile='" + groovyFile + "\'" + ", tomcatName='" + tomcatName + "\'" + "} " + super.toString();
    }

    public Path getDestinationDir() {
        return destinationDir;
    }

    public void setDestinationDir(Path destinationDir) {
        this.destinationDir = destinationDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getGroovyFile() {
        return groovyFile;
    }

    public void setGroovyFile(String groovyFile) {
        this.groovyFile = groovyFile;
    }

    public String getTomcatName() {
        return tomcatName;
    }

    public void setTomcatName(String tomcatName) {
        this.tomcatName = tomcatName;
    }

    private Path destinationDir;
    private String fileName = "camunda-bpm-tomcat-7.0.0-Final.zip";
    private String groovyFile = "groovy-all-2.2.0.jar";
    private String tomcatName;
}
