package betsy.virtual.host.exceptions.archive;

/**
 * The {@link ArchiveException} groups several errors that can occur if working
 * with archives.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public abstract class ArchiveException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public ArchiveException() {
		super();
	}

	public ArchiveException(final String message) {
		super(message);
	}

	public ArchiveException(final Throwable cause) {
		super(cause);
	}

	public ArchiveException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ArchiveException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
