package de.uniba.wiai.dsg.betsy.virtual.server.comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.Checksum;
import de.uniba.wiai.dsg.betsy.virtual.common.comm.CommPartner;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.CollectLogfileException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.ConnectionException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogRequest;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogfileCollection;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.StatusMessage;
import de.uniba.wiai.dsg.betsy.virtual.server.LogfileCollector;

//TODO JavaDoc
public class ClientHandler implements Runnable, CommPartner {

	private final Logger log = Logger.getLogger(getClass());
	private final CommServer commServer;

	private VirtualizedEngineDeployer deployer = null;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;

	private volatile boolean keepRunning = false;

	public ClientHandler(final CommServer commServer, final Socket socket)
			throws IOException {
		this.commServer = Objects.requireNonNull(commServer);
		this.socket = Objects.requireNonNull(socket);

		// client connected, now create all needed objects
		socket.setKeepAlive(true);

		OutputStream out = socket.getOutputStream();
		BufferedOutputStream bout = new BufferedOutputStream(out);
		this.oos = new ObjectOutputStream(bout);
		oos.flush();

		InputStream in = socket.getInputStream();
		BufferedInputStream bin = new BufferedInputStream(in);
		this.ois = new ObjectInputStream(bin);

		log.info("Connection to client fully established!");
	}

	@Override
	public void run() {
		try {
			Object o = ois.readObject();

			// receive PING or ENGINE
			handleInitialMessage(o);

			if (deployer == null) {
				// Incompatible, wrong answer, ... --> DISCONNECT
				// send disconnection reason
				this.sendMessage(StatusMessage.ERROR_INCOMPATIBLE_ENGINE);
				this.disconnect();
			} else {
				this.sendMessage(StatusMessage.OK);
			}

			while (keepRunning) {
				try {
					// read incoming messages, blocks until msg received
					o = ois.readObject();
					log.debug("New incoming message!");

					if (o instanceof StatusMessage) {
						StatusMessage sm = (StatusMessage) o;
						handleStatusMessage(sm);
					} else if (o instanceof DeployOperation) {
						DeployOperation dc = (DeployOperation) o;
						handleDeployContainer(dc);
					} else if (o instanceof LogRequest) {
						LogRequest lr = (LogRequest) o;
						handleLogRequest(lr);
					} else {
						// send invalid request message
						this.sendMessage(StatusMessage.ERROR_INVALID_REQUEST);
					}
				} catch (ClassNotFoundException exception) {
					// send invalid request message
					this.sendMessage(StatusMessage.ERROR_INVALID_REQUEST);
				}
			} // END WHILE
		} catch (ConnectionException | IOException exception) {
			log.error("Connection lost:", exception);
		} catch (ClassNotFoundException exception) {
			log.error("Unknown class received:", exception);
		} finally {
			// tidy up!
			tidyUp();
			// start listening again...
			commServer.waitForConnection();
		}
	}

	private void handleInitialMessage(Object o) throws IOException,
			ConnectionException {
		if (o != null && o instanceof String) {
			String message = (String) o;
			if (message.equals(CommPartner.PING)) {
				// test if connection is alive, respond:
				sendMessage(CommPartner.PONG);
			} else {
				deployer = VirtualizedEngineDeployers.build(message);
				keepRunning = true;
			}
		} else {
			// invalid response
			this.sendMessage(StatusMessage.ERROR_ENGINE_EXPECTED);
			this.disconnect();
		}
	}

	private void handleDeployContainer(DeployOperation dc) throws IOException,
			ConnectionException {
		log.debug("RCV deploy instructions");
		// checksum verification
		boolean dataValid = dc.getFileMessage().isDataValid();
		log.debug("Is checksum valid? " + dataValid);
		if (dataValid) {
			try {
				long start = -System.currentTimeMillis();
				deployer.deploy(dc);
				log.debug("Deployed! now wait on availability");

				deployer.onPostDeployment(dc);
				log.info("Deploy took: " + (System.currentTimeMillis() + start)
						+ "ms");
				this.sendMessage(StatusMessage.DEPLOYED);
			} catch (DeployException exception) {
				log.warn("Deployment failed, notifying client");
				this.sendMessage(StatusMessage.DEPLOY_FAILED);
			}
		} else {
			this.sendMessage(StatusMessage.ERROR_CHECKSUM);
		}
	}

	private void handleLogRequest(LogRequest container) throws IOException,
			ConnectionException {
		log.debug("Log request received");
		try {
			LogfileCollector collector = new LogfileCollector();
			LogfileCollection lfc = collector.collectLogfiles(container);
			this.sendMessage(lfc);
			log.info("Logfiles sent to client");
		} catch (CollectLogfileException e) {
			this.sendMessage(StatusMessage.ERROR_COLLECT_LOGFILES);
		}
	}

	private void handleStatusMessage(StatusMessage sm) throws IOException,
			ConnectionException {
		log.debug("RCV statusMessage: " + sm.toString());

		if (sm.equals(StatusMessage.REQUEST_IP)) {
			log.debug("Connected client has IP: "
					+ this.socket.getInetAddress().getHostAddress());
			this.sendMessage(this.socket.getInetAddress());
		} else if (sm.equals(StatusMessage.EXIT)) {
			// close connection --> quit while loop
			this.close();
		} else {
			// send invalid request message
			this.sendMessage(StatusMessage.ERROR_INVALID_REQUEST);
		}
	}

	@Override
	public boolean isConnected() {
		return socket != null && !socket.isClosed();
	}

	public void sendMessage(Object object) throws IOException,
			ConnectionException {
		if (isConnected()) {
			// send object
			this.oos.writeObject(object);
			this.oos.flush();
		} else {
			throw new ConnectionException("Sending message failed: "
					+ "no connection to a server established!");
		}
	}

	@Override
	public void disconnect() {
		log.debug("Properly disconnecting...");
		if (isConnected()) {
			try {
				sendMessage(StatusMessage.EXIT);
			} catch (IOException | ConnectionException exception) {
				// ignore, connection will be closed anyway
			} finally {
				this.close();
			}
		} else {
			this.close();
		}
	}
	
	public void close() {
		this.keepRunning = false;
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
