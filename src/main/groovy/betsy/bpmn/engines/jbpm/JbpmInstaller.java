package betsy.bpmn.engines.jbpm;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;

public class JbpmInstaller {

    private Path destinationDir;

    private String fileName = "jbpm-6.0.1.Final-installer-full.zip";

    public void install() {
        FileTasks.deleteDirectory(destinationDir);
        FileTasks.mkdirs(destinationDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        ConsoleTasks.setupAnt(getAntPath());

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), getAntPath().toAbsolutePath().toString() + "/ant -q install.jboss"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), getAntPath().toAbsolutePath().toString() + "/ant -q install.jboss"));

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), getAntPath().toAbsolutePath().toString() + "/ant -q install.jBPM-console.into.jboss"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), getAntPath().toAbsolutePath().toString() + "/ant -q install.jBPM-console.into.jboss"));
    }

    private Path getAntPath() {
        return Configuration.getAntHome().resolve("bin");
    }

    @Override
    public String toString() {
        return "JBPMInstaller{" + "destinationDir='" + destinationDir + "\'" + ", fileName='" + fileName + "\'" + "}";
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

}
