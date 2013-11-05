package betsy.virtual.host;

import org.apache.commons.lang.StringUtils;

import betsy.data.engines.Engine;

/**
 * A {@link ServiceAddress} is an address to a service that is mandatory for the
 * proper execution of an {@link Engine}. It is also possible to specify special
 * content that must be available on this address to show that the service is
 * available and ready for usage.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class ServiceAddress {

	private String address;
	private String requiredHtmlContent;

	public ServiceAddress(final String address, final String requiredHtmlContent) {
		this.setAddress(address);
		this.setRequiredHtmlContent(requiredHtmlContent);
	}

	public ServiceAddress(final String address) {
		this(address, null);
	}

	@Override
	public String toString() {
		return address;
	}

	public void setAddress(final String address) {
		if (StringUtils.isBlank(address)) {
			throw new IllegalArgumentException("Address must not be blank");
		}
		this.address = address;
	}

	public void setRequiredHtmlContent(final String requiredContent) {
		if (StringUtils.isBlank(requiredContent)) {
            // TODO warum hier keine exception, bei der Adresse aber schon?
			this.requiredHtmlContent = "";
		} else {
			this.requiredHtmlContent = requiredContent;
		}
	}

	public String getAddress() {
		return address;
	}

	public boolean isRequiringHtmlContent() {
		return !requiredHtmlContent.isEmpty();
	}

	public String getRequiredHtmlContent() {
		return requiredHtmlContent;
	}

}
