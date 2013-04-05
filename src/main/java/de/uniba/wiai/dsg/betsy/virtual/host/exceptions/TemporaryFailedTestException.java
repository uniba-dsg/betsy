package de.uniba.wiai.dsg.betsy.virtual.host.exceptions;

public class TemporaryFailedTestException extends IllegalStateException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	public TemporaryFailedTestException() {
	}

	public TemporaryFailedTestException(String s) {
		super(s);
	}

	public TemporaryFailedTestException(Throwable cause) {
		super(cause);
	}

	public TemporaryFailedTestException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
