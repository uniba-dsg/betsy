package betsy.bpel.engines.petalsesb;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class PetalsEsbDeployer {

    private final Path deploymentDirPath;
    private final Path logFilePath;
    private final Optional<Timeout> timeout;

    public PetalsEsbDeployer(Path deploymentDirPath, Path logFilePath) {
        this(deploymentDirPath, logFilePath, TimeoutRepository.getTimeout("PetalsEsbDeployer.constructor"));
    }

    public PetalsEsbDeployer(Path deploymentDirPath, Path logFilePath, Optional<Timeout> timeout) {
        this.deploymentDirPath = deploymentDirPath;
        this.logFilePath = logFilePath;
        this.timeout = timeout;
    }

    public void deploy(Path packageFilePath, String processName) {
        FileTasks.copyFileIntoFolder(packageFilePath, deploymentDirPath);

        WaitTasks.waitFor(timeout, () ->
                FileTasks.hasNoFile(deploymentDirPath.resolve(getFileName(processName))) &&
                        (FileTasks.hasFileSpecificSubstring(logFilePath,
                                "Service Assembly \'" + processName + "Application\' started"))
                //TODO handle failure as well (success vs. failure detection logic)
        );
    }

    private String getFileName(String processName) {
        return processName + "Application.zip";
    }

    public void undeploy(String processName) {
        FileTasks.deleteFile(deploymentDirPath.getParent().resolve("installed").resolve(getFileName(processName)));
        WaitTasks.waitForSubstringInFile(timeout, logFilePath,
                "Service Assembly \'" + processName + "Application\' undeployed");
    }

    public boolean isDeployed(String processName) {
        try {
            long foundFolders = Files.find(deploymentDirPath.getParent().resolve("repository").resolve("service-assemblies"), 1,
                    (p, a) -> Files.isDirectory(p) && (p).getFileName().toString().startsWith(processName + "Application-")).count();
            return foundFolders == 1;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}