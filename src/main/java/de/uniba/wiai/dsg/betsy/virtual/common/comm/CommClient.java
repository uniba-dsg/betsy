package de.uniba.wiai.dsg.betsy.virtual.common.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogfileCollection;
import de.uniba.wiai.dsg.betsy.virtual.exceptions.ChecksumException;
import de.uniba.wiai.dsg.betsy.virtual.exceptions.CollectLogfileException;
import de.uniba.wiai.dsg.betsy.virtual.exceptions.ConnectionException;
import de.uniba.wiai.dsg.betsy.virtual.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.exceptions.InvalidResponseException;

public interface CommClient extends CommPartner {

	public void reconnect(int timeout) throws UnknownHostException, IOException;

	public LogfileCollection getLogfilesFromServer(
			final String betsyInstallDir, final String engineInstallDir)
			throws ChecksumException, ConnectionException,
			InvalidResponseException, CollectLogfileException;

	public void sendDeploy(DeployContainer container) throws DeployException,
			ChecksumException, ConnectionException, InvalidResponseException;

	public boolean isConnectionAlive() throws InvalidResponseException;

	public void sendEngineInformation(final String engineName)
			throws InvalidResponseException, ConnectionException;

	public InetAddress getIpAddressFromServer()
			throws InvalidResponseException, ConnectionException;
	
}
