package de.uniba.wiai.dsg.betsy.virtual.host.comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.Checksum;
import de.uniba.wiai.dsg.betsy.virtual.common.comm.CommClient;
import de.uniba.wiai.dsg.betsy.virtual.common.comm.CommPartner;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.ChecksumException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.CollectLogfileException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.ConnectionException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.InvalidResponseException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.FileMessage;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogRequest;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogfileCollection;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.StatusMessage;

//TODO JAVADOC
public class TCPCommClient implements CommClient {

	private final String serverName;
	private final int serverPort;
	private final Logger log = Logger.getLogger(getClass());

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public TCPCommClient(final String serverName, final int serverPort) {
		// catch invalid values, null or empty
		if (serverName == null || serverName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"serverName must not be null or empty");
		} else if (serverPort <= 0 || serverPort > 65535) {
			throw new IllegalArgumentException(
					"serverPort must be between 1 and 65535");
		}

		this.serverName = serverName;
		this.serverPort = serverPort;
	}

	@Override
	public void reconnect(int timeout) throws IOException {
		log.debug("Reconnect to server...");
		if (!isConnected()) {
			// connect

			// make tmp in case of possible timeout
			this.socket = new Socket();
			this.socket.connect(new InetSocketAddress(serverName, serverPort),
					timeout);
			log.debug("socket to " + serverName + " created");

			if (timeout > 0) {
				log.debug("Set socket timeout to: " + timeout);
				socket.setSoTimeout(timeout);
			}
			socket.setKeepAlive(true);

			OutputStream out = socket.getOutputStream();
			BufferedOutputStream bout = new BufferedOutputStream(out);
			this.oos = new ObjectOutputStream(bout);
			this.oos.flush();

			InputStream in = socket.getInputStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			ois = new ObjectInputStream(bin);

			log.debug("... connection to server established!");
		} else {
			this.disconnect();
			this.reconnect(timeout);
		}
	}

	@Override
	public boolean isConnectionAlive() throws InvalidResponseException {
		try {
			sendMessage(CommPartner.PING);

			Object o = ois.readObject();
			if (o instanceof String) {
				String stringO = (String) o;
				log.debug("PING response received: '" + stringO + "'");
				if (o.equals(CommPartner.PONG)) {
					return true;
				}
			}

			log.fatal("Invalid response received upon PING request: '"
					+ o.getClass().toString() + "'");
			throw new InvalidResponseException(
					"Invalid response received upon PING request: '"
							+ o.getClass().toString() + "'");
		} catch (IOException | ConnectionException exception) {
			log.error("Exception in client while PING server:", exception);
			return false;
		} catch (ClassNotFoundException exception) {
			log.fatal("Invalid response received upon PING request:", exception);
			throw new InvalidResponseException(
					"Invalid response received upon PING request:", exception);
		}
	}

	@Override
	public void sendEngineInformation(final String engineName)
			throws InvalidResponseException, ConnectionException {
		Object o;
		try {
			sendMessage(engineName);

			// receive the answer
			o = ois.readObject();
			if (o instanceof StatusMessage) {
				StatusMessage sm = (StatusMessage) o;
				if (sm.equals(StatusMessage.OK)) {
					// engine is available at the VM
					return;
				} else {
					log.fatal("Invalid StatusMessage received upon sending engine name: '"
							+ sm.toString() + "'");
					throw new InvalidResponseException(
							"Invalid StatusMessage received upon sending engine name: '"
									+ sm.toString() + "'");
				}
			} else {
				log.fatal("Invalid response received upon sending engine name: '"
						+ o.getClass().toString() + "'");
				throw new InvalidResponseException(
						"Invalid response received upon sending engine name: '"
								+ o.getClass().toString() + "'");
			}
		} catch (ConnectionException | IOException exception) {
			log.error("Connection error in client while sending engine name:",
					exception);
			throw new ConnectionException(
					"Connection error in client while sending engine name:",
					exception);
		} catch (ClassNotFoundException exception) {
			log.fatal("Invalid response received upon sending engine name:",
					exception);
			throw new InvalidResponseException(
					"Invalid response received upon sending engine name:",
					exception);
		}

	}

	@Override
	public void disconnect() {
		if (isConnected()) {
			try {
				StatusMessage exitMsg = StatusMessage.EXIT;
				sendMessage(exitMsg);
			} catch (IOException | ConnectionException exception) {
				// ignore, connection will be closed anyway
			} finally {
				tidyUp();
			}
		} else {
			tidyUp();
		}
	}

	@Override
	public boolean isConnected() {
		return socket != null && !socket.isClosed();
	}

	private void sendMessage(Object object) throws IOException,
			ConnectionException {
		if (isConnected() && this.oos != null) {
			// send object
			this.oos.writeObject(object);
			this.oos.flush();
		} else {
			throw new ConnectException(
					"Sending message failed: no connection to a server established!");
		}
	}

	@Override
	public InetAddress getIpAddressFromServer()
			throws InvalidResponseException, ConnectionException {
		try {
			// send the request
			this.sendMessage(StatusMessage.REQUEST_IP);

			// receive the answer
			Object o = ois.readObject();
			if (o instanceof InetAddress) {
				log.debug("InetAddress received!");
				return (InetAddress) o;
			} else {
				log.fatal("Invalid response received upon IP request: '"
						+ o.getClass().toString() + "'");
				throw new InvalidResponseException(
						"Invalid response received upon IP request: '"
								+ o.getClass().toString() + "'");
			}
		} catch (IOException exception) {
			log.error(
					"Connection error in client while requesting IP address answer",
					exception);
			throw new ConnectionException(
					"Connection error in client while requesting IP address answer",
					exception);
		} catch (ClassNotFoundException exception) {
			log.fatal(
					"Invalid response received upon IP request: class could not be found",
					exception);
			throw new InvalidResponseException(
					"Invalid response received upon IP request: class could not be found'",
					exception);
		}

	}

	@Override
	public LogfileCollection getLogfilesFromServer(
			final String betsyInstallDir, final String engineInstallDir)
			throws ChecksumException, ConnectionException,
			InvalidResponseException, CollectLogfileException {
		try {
			// create request
			LogRequest lrc = new LogRequest(betsyInstallDir, engineInstallDir);
			// send the request
			this.sendMessage(lrc);

			// receive the answer
			Object o = ois.readObject();
			if (o instanceof LogfileCollection) {
				log.debug("Logfiles received, now save them");
				LogfileCollection logfiles = (LogfileCollection) o;

				// validate checksum of engine logs
				for (FileMessage lf : logfiles.getEngineLogfiles()) {
					if (!lf.getChecksum().equals(new Checksum(lf.getData()))) {
						throw new ChecksumException(
								"Checksum of logfile did not match");
					}
				}
				// validate checksum of betsy logs
				for (FileMessage lf : logfiles.getBetsyLogfiles()) {
					if (!lf.getChecksum().equals(new Checksum(lf.getData()))) {
						throw new ChecksumException(
								"Checksum of logfile did not match");
					}
				}

				// all logs are valid
				return logfiles;
			} else if (o instanceof StatusMessage) {
				StatusMessage sm = (StatusMessage) o;
				if (sm.equals(StatusMessage.NO_LOGFILE_AVAILABLE)) {
					return new LogfileCollection();
				} else if (sm.equals(StatusMessage.ERROR_COLLECT_LOGFILES)) {
					throw new CollectLogfileException("Couldn't collect "
							+ "logfiles from server.");
				} else {
					log.fatal("Invalid StatusMessage received upon logfile request: '"
							+ sm.toString() + "'");
					throw new InvalidResponseException(
							"Invalid StatusMessage received upon logfile request: '"
									+ sm.toString() + "'");
				}
			} else {
				log.fatal("Invalid response received upon logfile request: '"
						+ o.getClass().toString() + "'");
				throw new InvalidResponseException(
						"Invalid response received upon logfile request: '"
								+ o.getClass().toString() + "'");
			}
		} catch (IOException exception) {
			log.error(
					"Connection error in client while receiving logfile request answer",
					exception);
			throw new ConnectionException(
					"Connection error in client while receiving logfile request answer",
					exception);
		} catch (ClassNotFoundException exception) {
			log.fatal(
					"Invalid response received upon logfile request: class could not be found",
					exception);
			throw new InvalidResponseException(
					"Invalid response received upon logfile request: class could not be found'",
					exception);
		}
	}

	@Override
	public void sendDeploy(DeployOperation container) throws DeployException,
			ChecksumException, ConnectionException, InvalidResponseException {
		try {
			this.sendMessage(container);

			// wait for response
			Object o = ois.readObject();
			if (o instanceof StatusMessage) {

				log.debug("RCV STATUS...");
				StatusMessage sm = (StatusMessage) o;
				if (sm.equals(StatusMessage.DEPLOYED)) {
					log.debug("...DEPLOYED");
					// everything done, return
					return;
				} else if (sm.equals(StatusMessage.DEPLOY_FAILED)) {
					log.warn("...DEPLOY FAILED");
					throw new DeployException(
							"Could not deploy package, failed.");
				} else if (sm.equals(StatusMessage.ERROR_CHECKSUM)) {
					log.info("...DEPLOY CORRUPTED");
					throw new ChecksumException(
							"Could not deploy package, package was corrupted");
				} else {
					log.fatal("Invalid StatusMessage received upon sending deployment instructions: '"
							+ sm.toString() + "'");
					throw new InvalidResponseException(
							"Invalid StatusMessage received upon sending deployment instructions: '"
									+ sm.toString() + "'");
				}
			} else {
				log.fatal("Invalid response received upon sending deployment instructions "
						+ o.getClass().toString() + "'");
				throw new InvalidResponseException(
						"Invalid response received upon sending deployment instructions: '"
								+ o.getClass().toString() + "'");
			}
		} catch (IOException exception) {
			log.error(
					"Connection error in client while receiving answer to deployment instructions",
					exception);
			throw new ConnectionException(
					"Connection error in client while receiving answer to deployment instructions",
					exception);
		} catch (ClassNotFoundException exception) {
			log.fatal(
					"Invalid response received sending deployment instructions: class could not be found",
					exception);
			throw new InvalidResponseException(
					"Invalid response received upon sending deployment instructions: class could not be found'",
					exception);
		}
	}

	private void tidyUp() {
		if (this.oos != null) {
			try {
				oos.close();
			} catch (IOException exception) {
				// ignore
			} finally {
				oos = null;
			}
		}
		if (this.ois != null) {
			try {
				ois.close();
			} catch (IOException exception) {
				// ignore
			} finally {
				ois = null;
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException exception) {
				// ignore
			} finally {
				socket = null;
			}
		}
	}
}
