package betsy.common.virtual.exceptions;

/**
 * 
 * The class NetworkExceptions is subclass of {@link Exception} and so it's a form of Throwable.
 * In case of errors during network activities the application throws a TreatmentException.
 * 
 * @author Christoph Broeker
 * 
 * @version 1.0
 *
 */
public class NetworkException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7440877482438932467L;

	public NetworkException() {
		super();
	}

	public NetworkException(String arg0, Throwable arg1, boolean arg2,
							boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public NetworkException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NetworkException(String arg0) {
		super(arg0);
	}

	public NetworkException(Throwable arg0) {
		super(arg0);
	}

}