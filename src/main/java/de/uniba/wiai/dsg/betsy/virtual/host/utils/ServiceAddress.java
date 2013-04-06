package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import org.apache.commons.lang.StringUtils;

//TODO JavaDoc
public class ServiceAddress {

	private String address;
	private String requiredHtmlContent;

	public ServiceAddress(final String address, final String requiredHtmlContent) {
		this.setAddress(address);
		this.setRequiredHtmlContent(requiredHtmlContent);
	}
	
	public ServiceAddress(final String address) {
		this.setAddress(address);
		this.setRequiredHtmlContent(null);
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
