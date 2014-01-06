package betsy.config;


/**
 * The {@link ConfigurationException} is thrown if the {@link Configuration}
 * file did not contain mandatory information.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class ConfigurationException extends IllegalStateException {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(final String message) {
        super(message);
    }

    public ConfigurationException(final Throwable cause) {
        super(cause);
    }

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
