package betsy.virtual.server.deployers;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class DeploymentLogVerificator {

	private static final Logger log = Logger
			.getLogger(DeploymentLogVerificator.class);

	private final File logfile;
	private final String successMessage;
	private final String errorMessage;

	public DeploymentLogVerificator(final File file, final String successMsg,
			final String errorMsg) {
		this.successMessage = Objects.requireNonNull(successMsg);
		this.errorMessage = Objects.requireNonNull(errorMsg);
		this.logfile = Objects.requireNonNull(file);
		if (!file.isFile()) {
			throw new IllegalArgumentException("File does not exist");
		}
	}

	public DeploymentLogVerificator(final File file, final String successMsg) {
		this.successMessage = Objects.requireNonNull(successMsg);
		this.errorMessage = null;
		this.logfile = Objects.requireNonNull(file);
	}

	private boolean isDeploymentFinished() {
		boolean logVerification = false;

		try {
			log.debug("try log verification...");
			String fileContent = FileUtils.readFileToString(logfile);
			// try positive case
			logVerification = fileContent.contains(successMessage);
			// not confirmed? try negative case
			if (!logVerification) {
				logVerification = fileContent.contains(errorMessage);
			}
		} catch (IOException exception) {
			log.warn("Failed reading logfile:", exception);
		}

		return logVerification;
	}

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
