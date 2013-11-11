package betsy.virtual.common.exceptions;

/**
 * The {@link CollectLogfileException} is thrown if collecting the log files from
 * a server failed.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class CollectLogfileException extends CommunicationException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public CollectLogfileException() {
		super();
	}

	public CollectLogfileException(final String message) {
		super(message);
	}

	public CollectLogfileException(final Throwable cause) {
		super(cause);
	}

	public CollectLogfileException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CollectLogfileException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
