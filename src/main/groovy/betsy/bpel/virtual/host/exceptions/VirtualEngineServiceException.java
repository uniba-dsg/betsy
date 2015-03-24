package betsy.bpel.virtual.host.exceptions;

import betsy.bpel.virtual.host.VirtualBoxException;

/**
 * The {@link VirtualEngineServiceException} is thrown if a necessary
 * service is not available on the {@link betsy.bpel.virtual.host.virtualbox.VirtualBoxMachineImpl} and the
 * {@link betsy.bpel.virtual.host.engines.AbstractVirtualBPELEngine} can't be tested.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualEngineServiceException extends VirtualBoxException {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public VirtualEngineServiceException() {
		super();
	}

	public VirtualEngineServiceException(final String message) {
		super(message);
	}

	public VirtualEngineServiceException(final Throwable cause) {
		super(cause);
	}

	public VirtualEngineServiceException(final String message,
                                         final Throwable cause) {
		super(message, cause);
	}

	public VirtualEngineServiceException(final String message,
                                         final Throwable cause, final boolean enableSuppression,
                                         final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
