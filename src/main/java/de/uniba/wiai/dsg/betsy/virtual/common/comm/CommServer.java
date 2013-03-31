package de.uniba.wiai.dsg.betsy.virtual.common.comm;


public interface CommServer {

	public void waitForConnection();
	
	public void close();
	
}
