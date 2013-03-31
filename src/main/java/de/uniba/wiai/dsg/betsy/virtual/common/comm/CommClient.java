package de.uniba.wiai.dsg.betsy.virtual.common.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.ChecksumException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.CollectLogfileException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.ConnectionException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.InvalidResponseException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogfileCollection;

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
