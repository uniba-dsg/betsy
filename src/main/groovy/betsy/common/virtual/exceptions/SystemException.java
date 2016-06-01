package betsy.common.virtual.exceptions;

/**
 * 
 * The class {@link SystemException} is subclass of {@link Exception} and so it's a form of Throwable.
 * In case of generally errors in the running system  the application throws a TreatmentException.
 * 
 * @author Christoph Broeker
 * 
 * @version 1.0
 *
 */
public class SystemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7440877482438932467L;

	public SystemException() {
		super();
	}

	public SystemException(String arg0, Throwable arg1, boolean arg2,
						   boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public SystemException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SystemException(String arg0) {
		super(arg0);
	}

	public SystemException(Throwable arg0) {
		super(arg0);
	}
}