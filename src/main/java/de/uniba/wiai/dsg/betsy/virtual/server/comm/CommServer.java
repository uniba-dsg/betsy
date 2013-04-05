package de.uniba.wiai.dsg.betsy.virtual.server.comm;


public interface CommServer {

	public void waitForConnection();
	
	public void close();
	
}
