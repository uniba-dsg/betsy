package de.uniba.wiai.dsg.betsy.virtual.common.messages;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

//TODO JAVADOC
public class DeployOperation implements Serializable {

	/**
	 * SerialVersioUID.
	 */
	private static final long serialVersionUID = 1L;

	private String engineName;
	private String bpelFileNameWithoutExtension;

	private Integer deployTimeout;
	private String deploymentDir;
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

	public void setEngineName(String engineName) {
		if (StringUtils.isBlank(engineName)) {
			throw new IllegalArgumentException("serverType must not be null "
					+ "or empty");
		}
		this.engineName = engineName;
	}

	public void setBpelFileNameWithoutExtension(String bpelFileNameWithoutExtension) {
		if (StringUtils.isBlank(bpelFileNameWithoutExtension)) {
			throw new IllegalArgumentException("bpelFileNameWithoutExtension "
					+ "must not be null or empty");
		}
		this.bpelFileNameWithoutExtension = bpelFileNameWithoutExtension;
	}

	public void setDeployTimeout(Integer deployTimeout) {
		if (deployTimeout <= 0) {
			throw new IllegalArgumentException("deployTimeout must be greater "
					+ "than 0");
		}
		this.deployTimeout = deployTimeout;
	}

	public void setDeploymentDir(String deployDir) {
		if (StringUtils.isBlank(deployDir)) {
			throw new IllegalArgumentException("deployDir must not be null "
					+ "or empty");
		}
		this.deploymentDir = deployDir;
	}

	public void setEngineLogDir(String engineLogDir) {
		if (StringUtils.isBlank(engineLogDir)) {
			throw new IllegalArgumentException("engineLogDir must not be null "
					+ "or empty");
		}
		this.engineLogDir = engineLogDir;
	}

	public void setFileMessage(FileMessage fileMessage) {
		this.fileMessage = fileMessage;
	}

}
