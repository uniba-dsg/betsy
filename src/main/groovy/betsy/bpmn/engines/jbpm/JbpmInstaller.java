package betsy.bpmn.engines.jbpm;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;

public class JbpmInstaller {
    public void install() {
        FileTasks.deleteDirectory(destinationDir);
        FileTasks.mkdirs(destinationDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        Path antPath = Configuration.getAntHome().resolve("bin");
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir, antPath.toAbsolutePath().toString() + "/ant -q install.jboss"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir, antPath.toAbsolutePath().toString() + "/ant -q install.jboss"));

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir, antPath.toAbsolutePath().toString() + "/ant -q install.jBPM-console.into.jboss"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir, antPath.toAbsolutePath().toString() + "/ant -q install.jBPM-console.into.jboss"));
    }

    @Override
    public String toString() {
        return "JBPMInstaller{" + "destinationDir='" + destinationDir + "\'" + ", fileName='" + fileName + "\'" + "}";
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

    private Path destinationDir;
    private String fileName = "jbpm-installer.zip";
}
