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

    public TimeoutException(Timeout timeout) {
        this.timeout = Optional.ofNullable(timeout);
    }

    public TimeoutException(String message) {
        super(message);
        this.timeout = Optional.empty();
    }

    public TimeoutException(String message, Timeout timeout) {
        super(message);
        this.timeout = Optional.ofNullable(timeout);
    }

    public TimeoutException(String message, Throwable throwable) {
        super(message, throwable);
        this.timeout = Optional.empty();
    }

    public TimeoutException(String message, Throwable throwable, Timeout timeout) {
        super(message, throwable);
        this.timeout = Optional.ofNullable(timeout);
    }

    public TimeoutException(Throwable throwable) {
        super(throwable);
        this.timeout = Optional.empty();
    }

    public TimeoutException(Throwable throwable, Timeout timeout) {
        super(throwable);
        this.timeout = Optional.ofNullable(timeout);
    }

    public Optional<Timeout> getTimeout(){
            return timeout;
    }
}