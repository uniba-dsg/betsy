package de.uniba.wiai.dsg.betsy.virtual.common.messages;

import java.io.Serializable;

import de.uniba.wiai.dsg.betsy.virtual.common.Checksum;

//TODO JAVADOC
public class DeployContainer extends DataContainer implements Serializable {

	/**
	 * SerialVersioUID.
	 */
	private static final long serialVersionUID = 1L;

	private final String engineName;
	private final String bpelFileNameWithoutExtension;

	private final Integer deployTimeout;
	private final String deployDir;
	private final String engineLogDir;

	public DeployContainer(final String filename,
			final String bpelFileNameWithoutExtension, final byte[] data,
			String engineName, final Integer deployTimeout,
			final String deployDir, final String engineLogDir,
			final Checksum checksum) {
		// null check performed in DataContainer
		super(filename, data, checksum);
		if (engineName == null || engineName.trim().isEmpty()) {
			throw new IllegalArgumentException("serverType must not be null "
					+ "or empty");
		}
		if (deployDir == null || deployDir.trim().isEmpty()) {
			throw new IllegalArgumentException("deployDir must not be null "
					+ "or empty");
		}
		if (engineLogDir == null || engineLogDir.trim().isEmpty()) {
			throw new IllegalArgumentException("engineLogDir must not be null "
					+ "or empty");
		}
		if (deployTimeout <= 0) {
			throw new IllegalArgumentException("deployTimeout must be greater "
					+ "than 0");
		}
		if (bpelFileNameWithoutExtension == null
				|| bpelFileNameWithoutExtension.trim().isEmpty()) {
			throw new IllegalArgumentException("bpelFileNameWithoutExtension "
					+ "must not be null or empty");
		}

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

}
