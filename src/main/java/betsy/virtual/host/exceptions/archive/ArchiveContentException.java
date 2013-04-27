package betsy.virtual.host.exceptions.archive;

/**
 * The {@link ArchiveContentException} is thrown if the archive did not contain
 * a valid appliance that could be imported by VirtualBox.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class ArchiveContentException extends ArchiveException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public ArchiveContentException() {
		super();
	}

	public ArchiveContentException(final String message) {
		super(message);
	}

	public ArchiveContentException(final Throwable cause) {
		super(cause);
	}

	public ArchiveContentException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ArchiveContentException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
