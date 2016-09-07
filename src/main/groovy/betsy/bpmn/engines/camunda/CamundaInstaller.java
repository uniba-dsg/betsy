package betsy.bpmn.engines.camunda;

import java.nio.file.Path;
import java.util.Optional;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

public class CamundaInstaller {

    private Path destinationDir;

    private String fileName = "camunda-bpm-tomcat-7.0.0-Final.zip";

    private Optional<String> groovyFile = Optional.empty();

    private String tomcatName;

    public void install() {
        FileTasks.deleteDirectory(destinationDir);
        FileTasks.mkdirs(destinationDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        if (groovyFile.isPresent()) {
            NetworkTasks.downloadFileFromBetsyRepo(groovyFile.get());
            FileTasks.copyFileIntoFolder(Configuration.getDownloadsDir().resolve(groovyFile.get()), getTomcatDestinationDir().resolve("lib"));
        }

        //TODO use templates for creating these files
        FileTasks.createFile(destinationDir.resolve("camunda_startup.bat"), cdToTomcatBinFolder() + " && call startup.bat");
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.bat"), cdToTomcatBinFolder() + " && call shutdown.bat");

        //TODO use templates for creating these files
        FileTasks.createFile(destinationDir.resolve("camunda_startup.sh"), cdToTomcatBinFolder() + " && ./startup.sh");
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.sh"), cdToTomcatBinFolder() + " && ./shutdown.sh");

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", destinationDir.toAbsolutePath().toString()));
    }

    private String cdToTomcatBinFolder() {
        return "cd " + getTomcatBinFolder().toAbsolutePath();
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

    public void setDestinationDir(Path destinationDir) {
        this.destinationDir = destinationDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setGroovyFile(Optional<String> groovyFile) {
        this.groovyFile = groovyFile;
    }

    public void setTomcatName(String tomcatName) {
        this.tomcatName = tomcatName;
    }

}
