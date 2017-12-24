package betsy.common.timeouts;

import java.util.Objects;

import betsy.common.timeouts.timeout.Timeout;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutException extends RuntimeException {
    static final long serialVersionUID = -1848914673093119416L;

    private Timeout timeout;

    /**
     *
     * @param timeout The {@link Timeout}, which was the reason for this exception.
     */
    public TimeoutException(Timeout timeout) {
        super(Objects.requireNonNull(timeout).toString());
        this.timeout = timeout;
    }

    /**
     *
     * @return Return the {@link Timeout} of this Exception.
     */
    public Timeout getTimeout(){
        return timeout;
    }
}
