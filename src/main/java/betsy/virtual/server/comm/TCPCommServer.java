package betsy.virtual.server.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * TCP implementation of the {@link CommServer}.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class TCPCommServer implements CommServer {

	private final int port;

	private ServerSocket serverSocket;
	private ClientHandler clientHandler;

	private static final Logger log = Logger.getLogger(TCPCommServer.class);

	public TCPCommServer(final int port) {
		if (port <= 0 || port > 65535) {
			throw new IllegalArgumentException(
					"port must be between 1 and 65535");
		}
		this.port = port;
	}

	@Override
	public void handleNextConnection() {
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
			// run in current Thread
			clientHandler.run();
		} catch (IOException exception) {
			log.error("Exception while receiving connection: "
					+ "close and start listening again", exception);
		}

		log.debug("Current connection was closed.");
		// return to daemon loop...
	}

	@Override
	public void close() throws Exception {
		if (clientHandler != null) {
			clientHandler.close();
		}

		// close the socket!
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException ignore) {
				// ignore
			} finally {
				serverSocket = null;
			}
		}
	}

}
