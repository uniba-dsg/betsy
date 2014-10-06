package betsy.bpel.virtual.host.virtualbox.utils.port;

/**
 * The {@link PortUsageException} is thrown if a required port is already used
 * by another application.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class PortUsageException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public PortUsageException() {
		super();
	}

	public PortUsageException(final String message) {
		super(message);
	}

	public PortUsageException(final Throwable cause) {
		super(cause);
	}

	public PortUsageException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PortUsageException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
