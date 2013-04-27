package betsy.virtual.common.messages;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * A {@link LogRequest} tells the server to collect all the Logfiles on the
 * given location and return them to the client.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class LogRequest implements Serializable {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private final String betsyInstallDir;
	private final String engineLogfileDir;

	public LogRequest(final String betsyInstallDir,
			final String engineLogfileDir) {
		if (StringUtils.isBlank(betsyInstallDir)) {
			throw new IllegalArgumentException("betsyInstallDir must not be "
					+ "null or empty");
		}
		if (StringUtils.isBlank(engineLogfileDir)) {
			throw new IllegalArgumentException("engineLogfileDir must not be "
					+ "null or empty");
		}

		this.betsyInstallDir = betsyInstallDir;
		this.engineLogfileDir = engineLogfileDir;
	}

	public String getBetsyInstallDir() {
		return betsyInstallDir;
	}

	public String getEngineLogfileDir() {
		return engineLogfileDir;
	}

}
