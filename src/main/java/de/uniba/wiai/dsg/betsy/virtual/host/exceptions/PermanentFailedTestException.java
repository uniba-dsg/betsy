package de.uniba.wiai.dsg.betsy.virtual.host.exceptions;

public class PermanentFailedTestException extends IllegalStateException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	public PermanentFailedTestException() {
	}

	public PermanentFailedTestException(String s) {
		super(s);
	}

	public PermanentFailedTestException(Throwable cause) {
		super(cause);
	}

	public PermanentFailedTestException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
