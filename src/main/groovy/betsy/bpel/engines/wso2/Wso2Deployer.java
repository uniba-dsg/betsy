package betsy.bpel.engines.wso2;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;

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

        WaitTasks.waitFor(60_000, 500, () ->
                FileTasks.hasFileSpecificSubstring(logsDir.resolve("wso2carbon.log"), "{org.apache.ode.bpel.engine.BpelServerImpl} -  Registered process") ||
                FileTasks.hasFileSpecificSubstring(logsDir.resolve("wso2carbon.log"), "org.apache.axis2.deployment.DeploymentException: Error deploying BPEL package: " + file.getFileName().toString() + " {org.apache.axis2.deployment.DeploymentEngine}")
        );
        WaitTasks.sleep(2_000);
    }

    void undeploy(QName processId) {
        FileTasks.deleteFile(deploymentDir.resolve(processId.getLocalPart() + ".zip"));

        WaitTasks.waitForSubstringInFile(60_000, 500, logsDir.resolve("wso2carbon.log"), "Unregistered process " + processId.toString());
        WaitTasks.sleep(1_000);
    }

    boolean isDeployed(String filename) {
        return FileTasks.hasFile(deploymentDir.resolve(filename + ".zip"));
    }

}
