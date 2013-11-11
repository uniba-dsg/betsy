package betsy.virtual.common.exceptions;

/**
 * The {@link ConnectionException} is thrown if the connection between a client
 * and a server is interrupted or even lost.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class ConnectionException extends CommunicationException {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    public ConnectionException() {
        super();
    }

    public ConnectionException(final String message) {
        super(message);
    }

    public ConnectionException(final Throwable cause) {
        super(cause);
    }

    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(final String message, final Throwable cause,
                               final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
