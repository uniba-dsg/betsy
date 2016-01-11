package betsy.bpel.engines.activebpel;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.URLTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class ActiveBpelDeployer {

    private final Path deploymentFolder;
    private final Path logFile;
    private final Timeout timeout;

    public ActiveBpelDeployer(Path deploymentFolder, Path logFile) {
        this(deploymentFolder, logFile, TimeoutRepository.getTimeout("ActiveBpelDeployer.constructor"));
    }

    public ActiveBpelDeployer(Path deploymentFolder, Path logFile, Timeout timeout) {
        this.deploymentFolder = Objects.requireNonNull(deploymentFolder);
        this.logFile = Objects.requireNonNull(logFile);
        this.timeout = timeout;
    }

    public void deploy(Path packageFilePath, String processName) {
        FileTasks.copyFileIntoFolder(packageFilePath, deploymentFolder);

        WaitTasks.waitFor(timeout, () ->
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