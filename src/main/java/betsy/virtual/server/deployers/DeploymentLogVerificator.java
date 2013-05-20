package betsy.virtual.server.deployers;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Verifies the deployment of a WS-BPEL process by checking the engine's log
 * files. If the deployment has been processed, which also includes a failed
 * deployment, the verification is completed.<br>
 * In order to prevent deadlocks, a timeout defines the maximum time to wait.
 * 
 * @author Cedric Roeck
 * @version 1.0
 * 
 */
public class DeploymentLogVerificator {

	private static final Logger log = Logger
			.getLogger(DeploymentLogVerificator.class);

	private final File logfile;
	private final String successMessage;
	private final String errorMessage;

	public DeploymentLogVerificator(final File fileToCheck, final String successMsg,
			final String errorMsg) {
		this.successMessage = Objects.requireNonNull(successMsg);
		this.errorMessage = Objects.requireNonNull(errorMsg);
		this.logfile = Objects.requireNonNull(fileToCheck);
		if (!fileToCheck.isFile()) {
			throw new IllegalArgumentException("File does not exist");
		}
	}

	public DeploymentLogVerificator(final File fileToCheck, final String successMsg) {
		this.successMessage = Objects.requireNonNull(successMsg);
		this.errorMessage = null;
		this.logfile = Objects.requireNonNull(fileToCheck);
		if (!fileToCheck.isFile()) {
			throw new IllegalArgumentException("File does not exist");
		}
	}

	private boolean isDeploymentFinished() {
		boolean logVerification = false;

		try {
			log.debug("try log verification...");
			String fileContent = FileUtils.readFileToString(logfile);
			// try positive case
			logVerification = fileContent.contains(successMessage);
			// not confirmed? try negative case if is supported by the engine
			if (!logVerification && errorMessage != null) {
				logVerification = fileContent.contains(errorMessage);
			}
		} catch (IOException exception) {
			log.warn("Failed reading logfile:", exception);
		}

		return logVerification;
	}

	/**
	 * Wait for the completion a processes deployment, but never wait longer
	 * than the specified timeout.
	 * 
	 * @param timeoutInMs
	 *            max time to wait for completion in milliseconds
	 */
	public void waitForDeploymentCompletition(final int timeoutInMs) {
		final long start = -System.currentTimeMillis();
		// verify deployment with engine log. Either until deployment
		// result or until timeout is reached
		while (!isDeploymentFinished()
				&& (System.currentTimeMillis() + start < timeoutInMs)) {
			// not available yet? wait a little...
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		if (System.currentTimeMillis() + start > timeoutInMs) {
			log.trace("Deployment verification timed out");
		} else {
			log.trace("Log verification succesful");
		}
	}

}
