package betsy.common.virtual.swarm.host;

import betsy.common.virtual.exceptions.NetworkException;
import betsy.common.virtual.swarm.common.ConnectionService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * The {@link ConnectionHandler} is a thread that accepts new tcpConnections.
 * Starts for every connection a new {@link ConnectionService} and adds them to
 * ConnectionServiceList. Then the {@link ConnectionHandler} starts the
 * {@link TcpReceiver}.
 * 
 * @author Christoph Broeker
 * 
 * @version 1.0
 * 
 */
public class ConnectionHandler extends Thread {

	private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class);
	private ServerSocket serverSocket;
	private TcpMessageService tcpMessageService;
    private boolean isRunning = true;
    private CopyOnWriteArrayList<ConnectionService> connectionServiceList = new CopyOnWriteArrayList<>();

	/**
	 * 
	 * The {@link ConnectionHandler} is a thread that accepts new TCP-Connections. Starts for every connection a new
	 * {@link ConnectionService} and adds them to ConnectionServiceList. Then the {@link ConnectionHandler} starts the {@link TcpReceiver}.
	 * 
	 * 
	 * @param serverSocket The serverSocket on that the {@link ConnectionHandler} accepts the the new connections.
	 */
	public ConnectionHandler(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
        this.tcpMessageService = new TcpMessageService(connectionServiceList);
		while (isRunning) {
			try {
                ConnectionService connectionService = new ConnectionService(serverSocket.accept());
				connectionServiceList.add(connectionService);
                new TcpReceiver(connectionService,tcpMessageService).start();
            } catch (IOException | NetworkException  e) {
				LOGGER.info("Couldn't accept a new connection.");
			}
		}
	}

    /**
	 * This method stops the {@link ConnectionHandler}.
     *
     * @return Returns the {@link TcpMessageService}.
     */
    public synchronized TcpMessageService finish() {
        isRunning = false;
        return tcpMessageService;
    }
}
