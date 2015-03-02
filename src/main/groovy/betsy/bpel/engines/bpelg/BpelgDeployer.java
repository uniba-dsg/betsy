package betsy.bpel.engines.bpelg;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;

import java.nio.file.Path;

public class BpelgDeployer {

    private final Path deploymentFolder;
    private final Path logFile;
    private final int timeoutInSeconds;

    public BpelgDeployer(Path deploymentFolder, Path logFile) {
        this(deploymentFolder, logFile, 100);
    }

    public BpelgDeployer(Path deploymentFolder, Path logFile, int timeoutInSeconds) {
        this.deploymentFolder = deploymentFolder;
        this.logFile = logFile;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public void deploy(Path packageFilePath, String processName) {
        // deploy
        FileTasks.copyFileIntoFolder(packageFilePath, deploymentFolder);

        // ensure correct deployment
        WaitTasks.waitFor(timeoutInSeconds * 1000, 500, () ->
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
        WaitTasks.waitForSubstringInFile(timeoutInSeconds * 1000, 500, logFile, "Undeploying bpel: " + processName + ".bpel");
    }

    public boolean isDeployed(String processName) {
        return FileTasks.hasFile(deploymentFolder.resolve(processName + ".zip"));
    }

}
