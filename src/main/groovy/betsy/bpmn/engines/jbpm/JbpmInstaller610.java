package betsy.bpmn.engines.jbpm;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;

/**
 * Created by stssobetzko on 11.03.2015.
 */
public class JbpmInstaller610 extends JbpmInstaller {
    private Path destinationDir;
    private String fileName = "jbpm-6.1.0.Final-installer-full.zip";
    @Override
    public void install() {
        FileTasks.deleteDirectory(destinationDir);
        FileTasks.mkdirs(destinationDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        Path antPath = Configuration.getAntHome().resolve("bin");

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), antPath.toAbsolutePath().toString() + "/ant -q install.demo.noeclipse"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), antPath.toAbsolutePath().toString() + "/ant -q install.demo.noeclipse"));

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), antPath.toAbsolutePath().toString() + "/ant -q install.jBPM-console.into.jboss"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(destinationDir.resolve("jbpm-installer"), antPath.toAbsolutePath().toString() + "/ant -q install.jBPM-console.into.jboss"));


    }
    @Override
    public void setDestinationDir(Path destinationDir) {
        this.destinationDir = destinationDir;
    }

}
