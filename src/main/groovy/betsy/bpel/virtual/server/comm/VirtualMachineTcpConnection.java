package betsy.bpel.virtual.server.comm;

import betsy.bpel.virtual.common.Constants;
import betsy.bpel.virtual.common.Protocol;
import betsy.bpel.virtual.common.exceptions.CommunicationException;
import betsy.bpel.virtual.common.exceptions.ConnectionException;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployResponse;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

/**
 * The {@link VirtualMachineTcpConnection} manages the connection to a previously connected
 * client. The handler responds to the requests of the client. This includes
 * requests deploy a process as well as to send log files to the client. server.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualMachineTcpConnection extends ShutdownableThread {

    private static final Logger log = Logger.getLogger(VirtualMachineTcpConnection.class);

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;

    private final Protocol protocol;

    public VirtualMachineTcpConnection(final Socket socket, Protocol protocol) throws IOException {
        this.setName(this.getClass().getName());

        this.socket = Objects.requireNonNull(socket);
        this.protocol = Objects.requireNonNull(protocol);

        // client connected, now create all needed objects
        socket.setKeepAlive(true);

        OutputStream out = socket.getOutputStream();
        this.oos = new ObjectOutputStream(out);

        InputStream in = socket.getInputStream();
        this.ois = new ObjectInputStream(in);

        log.info("Connection to client fully established!");
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                Object o = receive();

                try {
                    if (o instanceof DeployRequest) {
                        DeployRequest request = (DeployRequest) o;
                        DeployResponse response = protocol.deployOperation(request);
                        this.sendMessage(response);
                    } else if (o instanceof LogFilesRequest) {
                        LogFilesRequest request = (LogFilesRequest) o;
                        LogFilesResponse response = protocol.collectLogFilesOperation(request);
                        this.sendMessage(response);
                    } else if (Constants.EXIT_REQUEST.equals(o)) {
                        this.shutdown();
                    } else {
                        throw new CommunicationException("Invalid request");
                    }
                } catch (Exception e) {
                    this.sendMessage(e);
                }
            }
        } catch (ConnectionException | IOException exception) {
            log.error("Connection lost:", exception);
        } catch (Error | Exception exception) {
            log.error("Unexpected error:", exception);
        } finally {
            // tidy up!
            tidyUp();
        }
    }

    private Object receive() throws IOException {
        try {
            // read incoming messages, blocks until msg received
            Object o = ois.readObject();
            log.debug("Message Received: " + o);
            return o;
        } catch (ClassNotFoundException exception) {
            // send invalid request message
            throw new CommunicationException("class not found", exception);
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    /**
     * Send a message back to the client.
     *
     * @param object message to send to the client
     * @throws IOException         thrown if there was an issue with the connection to the
     *                             client
     * @throws ConnectionException thrown if sending the message failed as there is no active
     *                             connection
     */
    public void sendMessage(Object object) throws IOException, ConnectionException {
        log.info("Sending Message: " + object);
        if (isConnected()) {
            // send object
            this.oos.writeObject(object);
            this.oos.flush();
        } else {
            throw new ConnectionException("Sending message failed: "
                    + "no connection to a server established!");
        }
    }

    /**
     * Disconnect the connection to the client and notify him.
     */
    public void disconnect() {
        log.debug("Properly disconnecting...");
        if (isConnected()) {
            try {
                sendMessage(Constants.EXIT_REQUEST);
            } catch (IOException | ConnectionException exception) {
                // ignore, connection will be closed anyway
            } finally {
                this.shutdown();
            }
        } else {
            this.shutdown();
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
