package betsy.bpel.engines.activebpel;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.URLTasks;
import betsy.common.tasks.WaitTasks;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class ActiveBpelDeployer {

    private final Path deploymentFolder;
    private final Path logFile;
    private final int timeoutInSeconds;

    public ActiveBpelDeployer(Path deploymentFolder, Path logFile) {
        this(deploymentFolder, logFile, 100);
    }

    public ActiveBpelDeployer(Path deploymentFolder, Path logFile, int timeoutInSeconds) {
        this.deploymentFolder = Objects.requireNonNull(deploymentFolder);
        this.logFile = Objects.requireNonNull(logFile);
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public void deploy(Path packageFilePath, String processName) {
        FileTasks.copyFileIntoFolder(packageFilePath, deploymentFolder);

        WaitTasks.waitFor(timeoutInSeconds * 1000, 500, () ->
                FileTasks.hasFile(deploymentFolder.resolve("work/ae_temp_" + processName + "_bpr/META-INF/catalog.xml")) &&
                        FileTasks.hasFileSpecificSubstring(logFile, "[" + processName + ".pdd]") &&
                        URLTasks.hasUrlSubstring(new URL("http://localhost:8080/BpelAdmin/"), "Running"));
    }


    public void undeploy(Path packageFilePath) {
        FileTasks.deleteFile(deploymentFolder.resolve(packageFilePath.getFileName()));
    }

    public boolean isDeployed(Path packageFilePath) {
        return FileTasks.hasFile(deploymentFolder.resolve(packageFilePath.getFileName()));
    }


}