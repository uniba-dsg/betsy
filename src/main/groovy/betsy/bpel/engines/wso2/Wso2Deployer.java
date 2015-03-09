package betsy.bpel.engines.wso2;

import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;

import javax.xml.namespace.QName;
import java.nio.file.Path;
import java.util.Objects;

public class Wso2Deployer {

    private final Path deploymentDir;
    private final Path logsDir;

    public Wso2Deployer(Path deploymentDir, Path logsDir) {
        this.deploymentDir = Objects.requireNonNull(deploymentDir);
        this.logsDir = Objects.requireNonNull(logsDir);
    }

    public void deploy(Path file) {
        FileTasks.copyFileIntoFolder(file, deploymentDir);

        WaitTasks.waitForSubstringInFile(60_000, 500, logsDir.resolve("wso2carbon.log"), "{org.apache.ode.bpel.engine.BpelServerImpl} -  Registered process");
        WaitTasks.sleep(20000);
    }

    void undeploy(QName processId) {
        FileTasks.deleteFile(deploymentDir.resolve(processId.getLocalPart() + ".zip"));

        WaitTasks.waitForSubstringInFile(60_000, 500, logsDir.resolve("wso2carbon.log"), "Unregistered process " + processId.toString());
        WaitTasks.sleep(1000);
    }

    boolean isDeployed(String filename) {
        return FileTasks.hasFile(deploymentDir.resolve(filename + ".zip"));
    }

}
