package betsy.virtual.host.exceptions.vm;

import betsy.virtual.host.VirtualBoxException;

/**
 * The {@link PortRedirectException} is thrown if a local port could not be
 * forwarded to the {@link betsy.virtual.host.virtualbox.VirtualBoxMachineImpl}.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class PortRedirectException extends VirtualBoxException {

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
