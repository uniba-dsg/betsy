package betsy.bpel.virtual.common.exceptions;

/**
 * The {@link DeployException} is thrown if the deployment of a
 * {@link betsy.bpel.virtual.common.messages.deploy.DeployRequest} failed.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class DeployException extends CommunicationException {

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
