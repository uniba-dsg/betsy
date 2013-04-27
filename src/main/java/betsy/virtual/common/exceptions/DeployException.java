package betsy.virtual.common.exceptions;

import betsy.virtual.common.messages.DeployOperation;

/**
 * The {@link DeployException} is thrown if the deployment of a
 * {@link DeployOperation} failed.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class DeployException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public DeployException() {
		super();
	}

	public DeployException(final String message) {
		super(message);
	}

	public DeployException(final Throwable cause) {
		super(cause);
	}

	public DeployException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DeployException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
