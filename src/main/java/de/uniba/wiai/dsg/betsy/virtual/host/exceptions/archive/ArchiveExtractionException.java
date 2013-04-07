package de.uniba.wiai.dsg.betsy.virtual.host.exceptions.archive;

/**
 * The {@link ArchiveContentException} is thrown if an Archive could not be
 * extracted properly. Causes can be interruptions, incomplete files or even a
 * CRC failure.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class ArchiveExtractionException extends ArchiveException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public ArchiveExtractionException() {
		super();
	}

	public ArchiveExtractionException(final String message) {
		super(message);
	}

	public ArchiveExtractionException(final Throwable cause) {
		super(cause);
	}

	public ArchiveExtractionException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public ArchiveExtractionException(final String message,
			final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
