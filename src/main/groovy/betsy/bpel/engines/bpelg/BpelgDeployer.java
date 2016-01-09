package betsy.bpel.engines.bpelg;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.util.FileTypes;
import timeouts.timeout.Timeout;
import timeouts.timeout.TimeoutRepository;

import java.nio.file.Path;
import java.util.Optional;

public class BpelgDeployer {

    private final Path deploymentFolder;
    private final Path logFile;
    private final Optional<Timeout> timeout;

    public BpelgDeployer(Path deploymentFolder, Path logFile, Optional<Timeout> timeout) {
        this.deploymentFolder = deploymentFolder;
        this.logFile = logFile;
        this.timeout = timeout;
    }
    public BpelgDeployer(Path deploymentFolder, Path logFile) {
        this(deploymentFolder, logFile, TimeoutRepository.getTimeout("BpelgDeployer.constructor"));
    }

    public void deploy(Path packageFilePath, String processName) {
        // deploy
        FileTasks.copyFileIntoFolder(packageFilePath, deploymentFolder);

        // ensure correct deployment
        WaitTasks.waitFor(timeout, () ->
                FileTasks.hasFile(deploymentFolder.resolve("work/ae_temp_" + processName + "_zip/deploy.xml")) &&
                        (
                                FileTasks.hasFileSpecificSubstring(logFile, "Deployment successful") ||
                                        FileTasks.hasFileSpecificSubstring(logFile, "Deployment failed")
                        )
        );
    }

    public void undeploy(String processName) {
        // undeploy
        FileTasks.deleteFile(deploymentFolder.resolve(processName + ".zip"));

        // ensure correct undeployment
        WaitTasks.waitForSubstringInFile(timeout, logFile, "Undeploying bpel: " + processName + FileTypes.BPEL);
    }

    public boolean isDeployed(String processName) {
        return FileTasks.hasFile(deploymentFolder.resolve(processName + ".zip"));
    }

}
