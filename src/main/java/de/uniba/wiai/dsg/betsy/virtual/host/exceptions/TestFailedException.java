package de.uniba.wiai.dsg.betsy.virtual.host.exceptions;

public class TestFailedException extends IllegalStateException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean repeatTest = false;
	
	public boolean isTestRepeatable() {
		return repeatTest;
	}
	
	public TestFailedException() {
	}

	public TestFailedException(String s) {
		super(s);
	}

	public TestFailedException(Throwable cause) {
		super(cause);
	}

	public TestFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TestFailedException(boolean repeatTest) {
		this.repeatTest = repeatTest;
	}

	public TestFailedException(String s, boolean repeatTest) {
		super(s);
		this.repeatTest = repeatTest;
	}

	public TestFailedException(Throwable cause, boolean repeatTest) {
		super(cause);
		this.repeatTest = repeatTest;
	}

	public TestFailedException(String message, Throwable cause, boolean repeatTest) {
		super(message, cause);
		this.repeatTest = repeatTest;
	}

}
