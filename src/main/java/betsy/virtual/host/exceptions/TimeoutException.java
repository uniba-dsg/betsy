package betsy.virtual.host.exceptions;

/**
 * The {@link TimeoutException} is thrown if an operation could not be performed
 * in the given time.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class TimeoutException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public TimeoutException() {
		super();
	}

	public TimeoutException(final String message) {
		super(message);
	}

	public TimeoutException(final Throwable cause) {
		super(cause);
	}

	public TimeoutException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TimeoutException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
