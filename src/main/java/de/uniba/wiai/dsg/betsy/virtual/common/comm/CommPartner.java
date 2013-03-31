package de.uniba.wiai.dsg.betsy.virtual.common.comm;


public interface CommPartner {

	public static final String PING = "BETSY-PING-MESSAGE";
	public static final String PONG = "BETSY-PONG-MESSAGE";
	
	public void disconnect();
	
	public boolean isConnected();
	
}