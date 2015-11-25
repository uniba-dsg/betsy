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

    public TimeoutException() {
    }

    public TimeoutException(Timeout timeout) {
        this.timeout = Optional.of(timeout);
    }

    public TimeoutException(String var1) {
        super(var1);
        timeout = Optional.empty();
    }

    public TimeoutException(String var1, Throwable var2) {
        super(var1, var2);
        timeout = Optional.empty();
    }

    public TimeoutException(Throwable var1) {
        super(var1);
        timeout = Optional.empty();
    }



    public Optional<Timeout> getTimeout(){
            return timeout;
    }
}