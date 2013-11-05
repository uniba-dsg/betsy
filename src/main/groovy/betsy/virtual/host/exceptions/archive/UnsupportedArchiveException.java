package betsy.virtual.host.exceptions.archive;

/**
 * The {@link UnsupportedArchiveException} is thrown if the archive to extract
 * is of an unsupported type.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class UnsupportedArchiveException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedArchiveException() {
		super();
	}

	public UnsupportedArchiveException(final String message) {
		super(message);
	}

	public UnsupportedArchiveException(final Throwable cause) {
		super(cause);
	}

	public UnsupportedArchiveException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public UnsupportedArchiveException(final String message,
			final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
