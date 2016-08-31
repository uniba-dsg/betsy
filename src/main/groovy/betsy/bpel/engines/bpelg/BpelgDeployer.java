package betsy.bpel.engines.bpelg;

import java.nio.file.Path;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.util.FileTypes;

public class BpelgDeployer {

    private final Path deploymentFolder;
    private final Path logFile;
    private final Timeout timeout;

    public BpelgDeployer(Path deploymentFolder, Path logFile, Timeout timeout) {
        this.deploymentFolder = deploymentFolder;
        this.logFile = logFile;
        this.timeout = timeout;
    }
    public BpelgDeployer(Path deploymentFolder, Path logFile) {
        this(deploymentFolder, logFile, TimeoutRepository.getTimeout("Bpelg.deploy"));
    }

    public void deploy(Path packageFilePath, String processName) {
        // deploy
        FileTasks.copyFileIntoFolder(packageFilePath, deploymentFolder);

        // ensure correct deployment
        timeout.waitFor(() ->
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
        timeout.waitForSubstringInFile(logFile, "Undeploying bpel: " + processName + FileTypes.BPEL);
    }

    public boolean isDeployed(String processName) {
        return FileTasks.hasFile(deploymentFolder.resolve(processName + ".zip"));
    }

}
