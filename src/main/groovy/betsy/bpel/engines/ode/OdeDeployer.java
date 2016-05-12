package betsy.bpel.engines.ode;

import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.ZipTasks;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.nio.file.Path;

public class OdeDeployer {

    private final Path deploymentFolder;
    private final Path logFile;
    private final Timeout timeout;

    public OdeDeployer(Path deploymentFolder, Path logFile, Timeout timeout) {
        this.deploymentFolder = deploymentFolder;
        this.logFile = logFile;
        this.timeout = timeout;
    }

    public OdeDeployer(Path deploymentFolder, Path logFile) {
        this(deploymentFolder, logFile, TimeoutRepository.getTimeout("Ode.deploy"));
    }

    public void deploy(Path packageFilePath, String processName) {
        FileTasks.deleteFile(getDeploymentIndicator(processName));
        ZipTasks.unzip(packageFilePath, getProcessFolder(processName));

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777",
                getProcessFolder(processName).toString()));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("sync"));

        timeout.waitFor(() -> FileTasks.hasFile(getDeploymentIndicator(processName)) &&
                (
                        FileTasks.hasFileSpecificSubstring(logFile, "Deployment of artifact " + processName + " successful") ||
                                FileTasks.hasFileSpecificSubstring(logFile, "Deployment of " + processName + " failed")
                ));
    }

    void undeploy(String processName) {
        FileTasks.deleteDirectory(getProcessFolder(processName));
        timeout.waitFor(() -> FileTasks.hasNoFile(getDeploymentIndicator(processName)));
    }

    boolean isDeployed(String processName) {
        return FileTasks.hasFile(getDeploymentIndicator(processName)) && FileTasks.hasFolder(getProcessFolder(processName));
    }

    private Path getProcessFolder(String processName) {
        return deploymentFolder.resolve(processName);
    }

    private Path getDeploymentIndicator(String processName) {
        return deploymentFolder.resolve(processName + ".deployed");
    }


}
