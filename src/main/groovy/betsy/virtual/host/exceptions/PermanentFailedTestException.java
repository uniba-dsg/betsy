package betsy.virtual.host.exceptions;

import betsy.data.engines.Engine;

/**
 * The {@link PermanentFailedTestException} is thrown if the test of an
 * {@link Engine}'s {@link Process} failed and the cause of the failure is
 * permanent. Each execution of the test will results in the same failure until
 * the cause of the error is eliminated.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class PermanentFailedTestException extends IllegalStateException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public PermanentFailedTestException() {
		super();
	}

	public PermanentFailedTestException(final String message) {
		super(message);
	}

	public PermanentFailedTestException(final Throwable cause) {
		super(cause);
	}

	public PermanentFailedTestException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

}
