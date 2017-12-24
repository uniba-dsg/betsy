package betsy.bpel.virtual.host;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

/**
 * A {@link ServiceAddress} is an address to a service that is mandatory for the
 * proper execution of an {@link betsy.bpel.engines.AbstractBPELEngine}. It is also possible to specify special
 * content that must be available on this address to show that the service is
 * available and ready for usage.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class ServiceAddress {

    private final URL address;
    private final String requiredHtmlContent;

    public ServiceAddress(final String address, final String requiredHtmlContent) {
        if (StringUtils.isBlank(address)) {
            throw new IllegalArgumentException("Address must not be blank");
        }

        if (requiredHtmlContent == null) {
            throw new IllegalArgumentException("html content must not be null");
        }

        try {
            this.address = new URL(address);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL " + address + " is not guilty", e);
        }
        this.requiredHtmlContent = requiredHtmlContent;
    }

    public ServiceAddress(final String address) {
        this(address, "");
    }

    @Override
    public String toString() {
        return address.toString();
    }

    public URL getAddress() {
        return address;
    }

    public boolean isRequiringHtmlContent() {
        return !requiredHtmlContent.isEmpty();
    }

    public String getRequiredHtmlContent() {
        return requiredHtmlContent;
    }

}
