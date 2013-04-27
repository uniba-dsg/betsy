package betsy.virtual.host.exceptions;

/**
 * The {@link DownloadException} is thrown if downloading a file, usually from a
 * remote source, failed.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class DownloadException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public DownloadException() {
		super();
	}

	public DownloadException(final String message) {
		super(message);
	}

	public DownloadException(final Throwable cause) {
		super(cause);
	}

	public DownloadException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DownloadException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
