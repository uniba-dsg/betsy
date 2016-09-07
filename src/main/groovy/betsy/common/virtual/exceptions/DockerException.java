package betsy.common.virtual.exceptions;


/**
 * @author Christoph Broeker
 * @version 1.0
 *
 */
public class DockerException extends RuntimeException {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    public DockerException() {
        super();
    }

    /**
     *
     * @param message The message for this exception.
     */
    public DockerException(String message) {
        super(message);
    }

    /**
     *
     * @param message The message for this exception.
     * @param throwable The {@link Throwable} of this exception.
     */
    public DockerException(String message, Throwable throwable) {
        super(message, throwable);
    }



    /**
     *
     * @param throwable The {@link Throwable} of this exception.
     */
    public DockerException(Throwable throwable) {
        super(throwable);
    }
}

