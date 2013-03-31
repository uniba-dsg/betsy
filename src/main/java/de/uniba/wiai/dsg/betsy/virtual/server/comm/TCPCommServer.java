package de.uniba.wiai.dsg.betsy.virtual.server.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.comm.CommServer;

// TODO JavaDoc
public class TCPCommServer implements CommServer {

	private final int port;

	private ServerSocket serverSocket;
	private ClientHandler clientHandler;

	private final Logger log = Logger.getLogger(getClass());

	public TCPCommServer(final int port) {
		if (port <= 0 || port > 65535) {
			throw new IllegalArgumentException(
					"port must be between 1 and 65535");
		}
		this.port = port;
	}

	@Override
	public void waitForConnection() {
		log.info("Server is now accepting a new connection");
		// create server socket only once
		if (serverSocket == null) {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException exception) {
				throw new RuntimeException("The Server could not be "
						+ "started: The server socket could not be opend!");
			}
		}

		// start waiting for connecting clients
		try {
			Socket socket = serverSocket.accept();
			clientHandler = new ClientHandler(this, socket);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String cmd = "ntpdate pool.ntp.org";
						Runtime run = Runtime.getRuntime();
						log.info("Requesting timeserver sync");
						Process pr = run.exec(cmd);
						pr.waitFor();
						BufferedReader buf = new BufferedReader(
								new InputStreamReader(pr.getInputStream()));

						String line = buf.readLine();
						while (line != null) {
							// TODO remove after test of setting system time
							log.debug(line);
							line = buf.readLine();
						}
					} catch (IOException | InterruptedException exception) {
						log.error("Exception while setting new system time",
								exception);
					}
				}
			}).start();

			// run in current Thread
			new Thread(clientHandler).run();

		} catch (IOException exception) {
			log.error("Exception while receiving connection: "
					+ "close and start listening again", exception);
		}

		log.debug("Current connection was closed.");
		// return to daemon loop...
	}

	@Override
	public void close() {

		if (clientHandler != null) {
			clientHandler.close();
		}

		// close the socket!
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException exception) {
				// ignore
			} finally {
				serverSocket = null;
			}
		}
	}

}
