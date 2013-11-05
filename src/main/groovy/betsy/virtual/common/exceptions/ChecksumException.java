package betsy.virtual.common.exceptions;

/**
 * The {@link ChecksumException} is thrown if the comparison of the received and
 * the expected data failed. Differences could be caused by network transport
 * errors or similar operations.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class ChecksumException extends Exception {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    public ChecksumException() {
        super();
    }

    public ChecksumException(final String message) {
        super(message);
    }

    public ChecksumException(final Throwable cause) {
        super(cause);
    }

    public ChecksumException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ChecksumException(final String message, final Throwable cause,
                             final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
