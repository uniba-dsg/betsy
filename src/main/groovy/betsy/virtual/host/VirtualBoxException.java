package betsy.virtual.host;

public class VirtualBoxException extends Exception {

    public VirtualBoxException() {
    }

    public VirtualBoxException(String message) {
        super(message);
    }

    public VirtualBoxException(String message, Throwable cause) {
        super(message, cause);
    }

    public VirtualBoxException(Throwable cause) {
        super(cause);
    }

    public VirtualBoxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
