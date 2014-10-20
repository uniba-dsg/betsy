package betsy.bpel.virtual.server.comm;

import betsy.bpel.virtual.common.Protocol;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualMachineTcpServer extends ShutdownableThread {

    private final int port;
    private final Protocol protocol;

    private static final Logger log = Logger.getLogger(VirtualMachineTcpServer.class);
    private ServerSocket serverSocket;

    public VirtualMachineTcpServer(final int port, final Protocol protocol) {
        this.setName(this.getClass().getName());

        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException(
                    "port must be between 1 and 65535");
        }
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server is now accepting a new connection");
            this.serverSocket = serverSocket;
            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    log.info("New connection accepted -> starting connection thread");
                    new VirtualMachineTcpConnection(socket, protocol).start();
                } catch (IOException ignore) {
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("The Server could not be "
                    + "started: The server socket could not be opened!", exception);
        }

        log.info("Server is shut down");
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignore) {
            }
        }
    }
}
