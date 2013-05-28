package betsy.virtual.host.exceptions;

import betsy.virtual.host.VirtualBoxException;
import betsy.virtual.host.engines.VirtualizedEngine;

/**
 * The {@link VirtualizedEngineServiceException} is thrown if a necessary
 * service is not available on the {@link betsy.virtual.host.virtualbox.VirtualBoxMachineImpl} and the
 * {@link VirtualizedEngine} can't be tested.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedEngineServiceException extends VirtualBoxException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public VirtualizedEngineServiceException() {
		super();
	}

	public VirtualizedEngineServiceException(final String message) {
		super(message);
	}

	public VirtualizedEngineServiceException(final Throwable cause) {
		super(cause);
	}

	public VirtualizedEngineServiceException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public VirtualizedEngineServiceException(final String message,
			final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
