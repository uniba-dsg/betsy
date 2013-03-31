package de.uniba.wiai.dsg.betsy.virtual.host.utils;

public class ServiceAddress {

	private String protocol;
	private String host;
	private String path;
	private int port;
	
	public ServiceAddress(final String protocol, final String host, final String path, int port) {
		this.setProtocol(protocol);
		this.setHost(host);
		this.setPath(path);
		this.setPort(port);
	}
	
	public ServiceAddress(final String protocol, final String host, final String path) {
		this.setProtocol(protocol);
		this.setHost(host);
		this.setPath(path);
		this.setNoPort();
	}
	
	public ServiceAddress(final String protocol, final String host) {
		this.setProtocol(protocol);
		this.setHost(host);
		this.setPath("");
		this.setNoPort();
	}
	
	public ServiceAddress(final String protocol, final String host, final int port) {
		this.setProtocol(protocol);
		this.setHost(host);
		this.setPath("");
		this.setPort(port);
	}
	
	@Override
	public String toString() {
		String protocol = this.protocol.endsWith("://") ? "" : this.protocol + "://" ;
		String port = this.port > 0 ? ":" + Integer.valueOf(this.port) : "";
		String path = this.path.startsWith("/") ? this.path : "/" + this.path ;
		return protocol + host + port + path;
	}
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		if(protocol == null || protocol.isEmpty()) {
			throw new IllegalArgumentException("Protocol must not be null or empty");
		}
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if(host == null || host.isEmpty()) {
			throw new IllegalArgumentException("Host must not be null or empty");
		}
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(path == null) {
			throw new IllegalArgumentException("Path must not be null");
		}
		this.path = path;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if(port < 1 || port > 65535) {
			throw new IllegalArgumentException("The port must be between 1 and 65535");
		}
		this.port = port;
	}
	
	public void setNoPort() {
		this.port = -1;
	}
}
