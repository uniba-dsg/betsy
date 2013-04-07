package de.uniba.wiai.dsg.betsy.virtual.host.exceptions;

import betsy.data.engines.Engine;

/**
 * The {@link TemporaryFailedTestException} is thrown if the test of an
 * {@link Engine}'s {@link Process} failed and the cause of the failure is non
 * permanent. The test can be repeated and might be successful the next time.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class TemporaryFailedTestException extends IllegalStateException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public TemporaryFailedTestException() {
		super();
	}

	public TemporaryFailedTestException(final String message) {
		super(message);
	}

	public TemporaryFailedTestException(final Throwable cause) {
		super(cause);
	}

	public TemporaryFailedTestException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

}
