package de.uniba.wiai.dsg.betsy.virtual.common.messages;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class LogRequest implements Serializable {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private final String betsyInstallDir;
	private final String engineInstallDir;

	public LogRequest(final String betsyInstallDir,
			final String engineInstallDir) {
		if (StringUtils.isBlank(betsyInstallDir)) {
			throw new IllegalArgumentException("betsyInstallDir must not be "
					+ "null or empty");
		}
		if (StringUtils.isBlank(engineInstallDir)) {
			throw new IllegalArgumentException("engineInstallDir must not be "
					+ "null or empty");
		}

		this.betsyInstallDir = betsyInstallDir;
		this.engineInstallDir = engineInstallDir;
	}

	public String getBetsyInstallDir() {
		return betsyInstallDir;
	}

	public String getEngineInstallDir() {
		return engineInstallDir;
	}

}
