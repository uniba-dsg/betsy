package betsy.virtual.common.exceptions;

/**
 * The {@link InvalidResponseException} is thrown if the response on a
 * communication request is unexpected. This means another answer (object type)
 * has been expected.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class InvalidResponseException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public InvalidResponseException() {
		super();
	}

	public InvalidResponseException(final String message) {
		super(message);
	}

	public InvalidResponseException(final Throwable cause) {
		super(cause);
	}

	public InvalidResponseException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InvalidResponseException(final String message,
			final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
