package betsy.bpel.virtual.common.messages.deploy;

import java.io.Serializable;
import java.util.Objects;

import betsy.common.timeouts.timeout.Timeout;

/**
 * A {@link DeployRequest} contains all relevant information for the
 * deployment of an EngineExtended's {@link Process}.<br>
 * The binaries are included as a {@link FileMessage}. Everything else, such as
 * where to deploy, which executable to use and where the logs are located, can
 * be set according to the engine the {@link Process} belongs to and the
 * operating system the engine is installed on.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class DeployRequest implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;


    private String engineName;
    private String processName;

    private Timeout deployTimeout;

    private String deploymentDir;
    private String deploymentLogFilePath;

    private FileMessage fileMessage;

    public String getProcessName() {
        return this.processName;
    }

    public Timeout getDeployTimeout() {
        return deployTimeout;
    }

    public String getDeploymentDir() {
        return deploymentDir;
    }

    public String getEngineLogfileDir() {
        return deploymentLogFilePath;
    }

    public FileMessage getFileMessage() {
        return fileMessage;
    }

    /**
     * Set the engine name.
     *
     * @param engineName name to set, must not be null or empty.
     */
    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    /**
     * Set the name of the BPEL file.
     *
     * @param processName name to set, must not be null or empty.
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * Set how long the deployment may take at max.
     *
     * @param deployTimeout maximum deployment duration, must be greater than 0
     */
    public void setDeployTimeout(Timeout deployTimeout) {
        this.deployTimeout = deployTimeout;
    }

    /**
     * Set the directory to deploy the files to
     *
     * @param deployDir dir to set, must not be null or empty.
     */
    public void setDeploymentDir(String deployDir) {
        this.deploymentDir = deployDir;
    }

    /**
     * Set the directory where the engine's log files are stored.
     *
     * @param deploymentLogFilePath directory to set, must not be null or empty.
     */
    public void setDeploymentLogFilePath(String deploymentLogFilePath) {
        this.deploymentLogFilePath = deploymentLogFilePath;
    }

    public void setFileMessage(FileMessage fileMessage) {
        this.fileMessage = Objects.requireNonNull(fileMessage);
    }

    public String getEngineName() {
        return engineName;
    }

    @Override
    public String toString() {
        return "DeployRequest{" +
                "engineName='" + engineName + '\'' +
                ", processName='" + processName + '\'' +
                ", deployTimeout=" + deployTimeout +
                ", deploymentDir='" + deploymentDir + '\'' +
                ", deploymentLogFilePath='" + deploymentLogFilePath + '\'' +
                ", fileMessage=" + fileMessage +
                '}';
    }
}
