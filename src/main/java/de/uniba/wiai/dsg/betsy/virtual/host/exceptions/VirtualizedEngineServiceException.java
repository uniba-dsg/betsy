package de.uniba.wiai.dsg.betsy.virtual.host.exceptions;

import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualMachine;

/**
 * The {@link VirtualizedEngineServiceException} is thrown if a necessary
 * service is not available on the {@link VirtualMachine} and the
 * {@link VirtualEngine} can't be tested.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedEngineServiceException extends Exception {

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
