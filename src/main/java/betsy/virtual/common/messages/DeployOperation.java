package betsy.virtual.common.messages;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

/**
 * A {@link DeployOperation} contains all relevant information for the
 * deployment of an Engine's {@link Process}.<br>
 * The binaries are included as a {@link FileMessage}. Everything else, such as
 * where to deploy, which executable to use and where the logs are located, can
 * be set according to the engine the {@link Process} belongs to and the
 * operating system the engine is installed on.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class DeployOperation implements Serializable {

	/**
	 * SerialVersioUID.
	 */
	private static final long serialVersionUID = 1L;

	private String engineName;
	private String bpelFileNameWithoutExtension;

	private Integer deployTimeout;
	private String deploymentDir;
	private String deploymentFile;
	private String engineLogDir;

	private FileMessage fileMessage;

	public String getEngineName() {
		return this.engineName;
	}

	public String getBpelFileNameWithoutExtension() {
		return this.bpelFileNameWithoutExtension;
	}

	public Integer getDeployTimeout() {
		return deployTimeout;
	}

	public String getDeploymentDir() {
		return deploymentDir;
	}

	public String getEngineLogfileDir() {
		return engineLogDir;
	}

	public FileMessage getFileMessage() {
		return fileMessage;
	}

	public String getDeploymentFile() {
		return deploymentFile;
	}

	/**
	 * Set the engine name.
	 * 
	 * @param engineName
	 *            name to set, must not be null or empty.
	 */
	public void setEngineName(String engineName) {
		if (StringUtils.isBlank(engineName)) {
			throw new IllegalArgumentException("serverType must not be null "
					+ "or empty");
		}
		this.engineName = engineName;
	}

	/**
	 * Set the name of the BPEL file.
	 * 
	 * @param bpelFileNameWithoutExtension
	 *            name to set, must not be null or empty.
	 */
	public void setBpelFileNameWithoutExtension(
			String bpelFileNameWithoutExtension) {
		if (StringUtils.isBlank(bpelFileNameWithoutExtension)) {
			throw new IllegalArgumentException("bpelFileNameWithoutExtension "
					+ "must not be null or empty");
		}
		this.bpelFileNameWithoutExtension = bpelFileNameWithoutExtension;
	}

	/**
	 * Set how long the deployment may take at max.
	 * 
	 * @param deployTimeout
	 *            maximum deployment duration, must be greater than 0
	 */
	public void setDeployTimeout(Integer deployTimeout) {
		if (deployTimeout <= 0) {
			throw new IllegalArgumentException("deployTimeout must be greater "
					+ "than 0");
		}
		this.deployTimeout = deployTimeout;
	}

	/**
	 * Set the directory to deploy the files to
	 * 
	 * @param deployDir
	 *            dir to set, must not be null or empty.
	 */
	public void setDeploymentDir(String deployDir) {
		if (StringUtils.isBlank(deployDir)) {
			throw new IllegalArgumentException("deployDir must not be null "
					+ "or empty");
		}
		this.deploymentDir = deployDir;
	}

	/**
	 * Set the directory where the engine's logfiles are stored.
	 * 
	 * @param engineLogDir
	 *            directory to set, must not be null or empty.
	 */
	public void setEngineLogDir(String engineLogDir) {
		if (StringUtils.isBlank(engineLogDir)) {
			throw new IllegalArgumentException("engineLogDir must not be null "
					+ "or empty");
		}
		this.engineLogDir = engineLogDir;
	}

	public void setFileMessage(FileMessage fileMessage) {
		this.fileMessage = Objects.requireNonNull(fileMessage);
	}

	/**
	 * Set the path to the file that must be used for deploying the
	 * process.
	 * 
	 * @param deploymentFile
	 *            path to set, must not be null or empty
	 */
	public void setDeploymentFile(String deploymentFile) {
		if (StringUtils.isBlank(deploymentFile)) {
			throw new IllegalArgumentException("deploymentExecutable must not "
					+ "be null or empty");
		}
		this.deploymentFile = deploymentFile;
	}

}
