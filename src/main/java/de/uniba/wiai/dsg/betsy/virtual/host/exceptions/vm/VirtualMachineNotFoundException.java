package de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm;

/**
 * The {@link VirtualMachineNotFoundException} is thrown if the VirtualBox
 * instance did not contain a virtual machine with the requested name or uuid.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualMachineNotFoundException extends Exception {

	/**
	 * SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public VirtualMachineNotFoundException() {
		super();
	}

	public VirtualMachineNotFoundException(final String message) {
		super(message);
	}

	public VirtualMachineNotFoundException(final Throwable cause) {
		super(cause);
	}

	public VirtualMachineNotFoundException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public VirtualMachineNotFoundException(final String message,
			final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
