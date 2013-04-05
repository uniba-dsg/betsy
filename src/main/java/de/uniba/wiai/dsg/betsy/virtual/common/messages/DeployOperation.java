package de.uniba.wiai.dsg.betsy.virtual.common.messages;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import de.uniba.wiai.dsg.betsy.virtual.common.Checksum;

//TODO JAVADOC
public class DeployOperation implements Serializable {

	/**
	 * SerialVersioUID.
	 */
	private static final long serialVersionUID = 1L;

	private final String engineName;
	private final String bpelFileNameWithoutExtension;

	private final Integer deployTimeout;
	private final String deployDir;
	private final String engineLogDir;

	private final FileMessage fileMessage;

	public DeployOperation(final String filename,
			final String bpelFileNameWithoutExtension, final byte[] data,
			String engineName, final Integer deployTimeout,
			final String deployDir, final String engineLogDir,
			final Checksum checksum) {
		if (StringUtils.isBlank(engineName)) {
			throw new IllegalArgumentException("serverType must not be null "
					+ "or empty");
		}
		if (StringUtils.isBlank(deployDir)) {
			throw new IllegalArgumentException("deployDir must not be null "
					+ "or empty");
		}
		if (StringUtils.isBlank(engineLogDir)) {
			throw new IllegalArgumentException("engineLogDir must not be null "
					+ "or empty");
		}
		if (deployTimeout <= 0) {
			throw new IllegalArgumentException("deployTimeout must be greater "
					+ "than 0");
		}
		if (StringUtils.isBlank(bpelFileNameWithoutExtension)) {
			throw new IllegalArgumentException("bpelFileNameWithoutExtension "
					+ "must not be null or empty");
		}

		// null check performed in DataContainer
		this.fileMessage = new FileMessage(filename, data, checksum);

		this.engineName = engineName;
		this.bpelFileNameWithoutExtension = bpelFileNameWithoutExtension;
		this.deployTimeout = deployTimeout;
		this.deployDir = deployDir;
		this.engineLogDir = engineLogDir;
	}

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
		return deployDir;
	}

	public String getEngineLogfileDir() {
		return engineLogDir;
	}

	public FileMessage getFileMessage() {
		return fileMessage;
	}

}
