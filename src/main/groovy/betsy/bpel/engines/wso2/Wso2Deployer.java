package betsy.bpel.engines.wso2;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import timeouts.timeout.TimeoutRepository;

import javax.xml.namespace.QName;
import java.nio.file.Path;
import java.util.Objects;

public class Wso2Deployer {

    private static final Logger LOGGER = Logger.getLogger(Wso2Deployer.class);

    private final Path deploymentDir;
    private final Path logsDir;

    public Wso2Deployer(Path deploymentDir, Path logsDir) {
        this.deploymentDir = Objects.requireNonNull(deploymentDir);
        this.logsDir = Objects.requireNonNull(logsDir);
    }

    public void deploy(Path file) {
        FileTasks.copyFileIntoFolder(file, deploymentDir);

        WaitTasks.waitFor(TimeoutRepository.getTimeout("Wso2Deployer.deploy.waitFor"), () ->
                FileTasks.hasFileSpecificSubstring(logsDir.resolve("wso2carbon.log"), "{org.apache.ode.bpel.engine.BpelServerImpl} -  Registered process {http://dsg.wiai.uniba.de/betsy") ||
                FileTasks.hasFileSpecificSubstring(logsDir.resolve("wso2carbon.log"), "org.apache.axis2.deployment.DeploymentException: Error deploying BPEL package: " + file.getFileName().toString() + " {org.apache.axis2.deployment.DeploymentEngine}")
        );
        WaitTasks.sleep(TimeoutRepository.getTimeout("Wso2Deployer.deploy.sleep").get().getTimeoutInMs());
    }

    void undeploy(QName processId) {
        FileTasks.deleteFile(deploymentDir.resolve(processId.getLocalPart() + ".zip"));

        WaitTasks.waitForSubstringInFile(TimeoutRepository.getTimeout("Wso2Deployer.undeploy.waitFor"), logsDir.resolve("wso2carbon.log"), "Unregistered process " + processId.toString());
        WaitTasks.sleep(TimeoutRepository.getTimeout("Wso2Deployer.undeploy.sleep").get().getTimeoutInMs());
    }

    boolean isDeployed(String filename) {
        return FileTasks.hasFile(deploymentDir.resolve(filename + ".zip"));
    }

}
