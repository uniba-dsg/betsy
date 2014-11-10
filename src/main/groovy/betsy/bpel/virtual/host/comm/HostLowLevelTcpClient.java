package betsy.bpel.virtual.host.comm;

import betsy.bpel.virtual.common.Constants;
import betsy.bpel.virtual.common.exceptions.CommunicationException;
import betsy.bpel.virtual.common.exceptions.ConnectionException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HostLowLevelTcpClient implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(HostLowLevelTcpClient.class);

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private final String host;
    private final int port;

    public HostLowLevelTcpClient(final String host, final int port) {
        // catch invalid values, null or empty
        if (StringUtils.isBlank(host)) {
            throw new IllegalArgumentException(
                    "host must not be null or empty");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException(
                    "port must be between 1 and 65535");
        }

        this.host = host;
        this.port = port;
    }

    public void sendMessage(Object message) throws ConnectionException {
        LOGGER.debug("Sending Message: " + message.toString());
        if (isConnected() && this.oos != null) {
            // send message
            try {
                this.oos.writeObject(message);
                this.oos.flush();
            } catch (IOException e) {
                throw new ConnectionException("Sending message failed", e);
            }
        } else {
            throw new ConnectionException("Sending message failed: no connection"
                    + " to a server established!");
        }
    }

    public void reconnect() throws IOException {
        reconnect(0);
    }

    public void reconnect(int timeout) throws IOException {
        if (!isConnected()) {
            LOGGER.info("Connecting to server");
            // connect
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(host, port),
                    timeout);

            socket.setSoTimeout(timeout);
            socket.setKeepAlive(true);

            OutputStream out = socket.getOutputStream();
            this.oos = new ObjectOutputStream(out);

            InputStream in = socket.getInputStream();
            this.ois = new ObjectInputStream(in);

            LOGGER.info("Connection to server established!");
        } else {
            LOGGER.info("Already connected - reconnecting");
            this.disconnect();
            this.reconnect(timeout);
        }
    }

    public Object receive() {
        try {
            Object object = null;
            boolean waitForMessage = true;
            while (waitForMessage) {
                try {
                    object = ois.readObject();
                    waitForMessage = false;
                } catch (SocketTimeoutException e) {
                    LOGGER.debug("Timeout occurred - waiting again");
                }
            }
            LOGGER.info("Received Message: " + object);
            return object;
        } catch (ClassNotFoundException e) {
            throw new CommunicationException("Invalid response received", e);
        } catch (IOException e) {
            throw new ConnectionException("Could not receive a message", e);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                sendMessage(Constants.EXIT_REQUEST);
            } catch (ConnectionException exception) {
                // ignore, connection will be closed anyway
            } finally {
                tidyUp();
            }
        } else {
            tidyUp();
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
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

    @Override
    public void close() {
        this.disconnect();
    }

}
