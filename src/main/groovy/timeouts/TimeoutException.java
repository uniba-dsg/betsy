package timeouts;


import timeouts.timeout.Timeout;

import java.util.Optional;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutException extends RuntimeException {
    static final long serialVersionUID = -1848914673093119416L;

    private Optional<Timeout> timeout;

    /**
     *
     * @param timeout The {@link Timeout}, which was the reason for this exception.
     */
    public TimeoutException(Timeout timeout) {
        this.timeout = Optional.ofNullable(timeout);
    }

    /**
     *
     * @param message The message for this exception.
     */
    public TimeoutException(String message) {
        super(message);
        this.timeout = Optional.empty();
    }

    /**
     *
     * @param message The message for this exception.
     * @param timeout The {@link Timeout}, which was the reason for this exception.
     */
    public TimeoutException(String message, Timeout timeout) {
        super(message);
        this.timeout = Optional.ofNullable(timeout);
    }

    /**
     *
     * @param message The message for this exception.
     * @param throwable The {@link Throwable} of this exception.
     */
    public TimeoutException(String message, Throwable throwable) {
        super(message, throwable);
        this.timeout = Optional.empty();
    }

    /**
     *
     * @param message The message for this exception.
     * @param throwable The {@link Throwable} of this exception.
     * @param timeout The {@link Timeout}, which was the reason for this exception.
     */
    public TimeoutException(String message, Throwable throwable, Timeout timeout) {
        super(message, throwable);
        this.timeout = Optional.ofNullable(timeout);
    }

    /**
     *
     * @param throwable The {@link Throwable} of this exception.
     */
    public TimeoutException(Throwable throwable) {
        super(throwable);
        this.timeout = Optional.empty();
    }

    /**
     *
     * @param throwable The {@link Throwable} of this exception.
     * @param timeout The {@link Timeout}, which was the reason for this exception.
     */
    public TimeoutException(Throwable throwable, Timeout timeout) {
        super(throwable);
        this.timeout = Optional.ofNullable(timeout);
    }

    /**
     *
     * @return Return the {@link Timeout} of this Exception.
     */
    public Optional<Timeout> getTimeout(){
            return timeout;
    }
}