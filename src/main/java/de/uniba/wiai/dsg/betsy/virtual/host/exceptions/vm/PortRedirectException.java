package de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm;

import de.uniba.wiai.dsg.betsy.virtual.host.VirtualMachine;

/**
 * The {@link PortRedirectException} is thrown if a local port could not be
 * forwarded to the {@link VirtualMachine}.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class PortRedirectException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public PortRedirectException() {
		super();
	}

	public PortRedirectException(final String message) {
		super(message);
	}

	public PortRedirectException(final Throwable cause) {
		super(cause);
	}

	public PortRedirectException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PortRedirectException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
