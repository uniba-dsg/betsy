package betsy.virtual.host.comm;

import betsy.virtual.common.Constants;
import betsy.virtual.common.Protocol;
import betsy.virtual.common.exceptions.CommunicationException;
import betsy.virtual.common.exceptions.ConnectionException;
import betsy.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.virtual.common.messages.deploy.DeployRequest;
import betsy.virtual.common.messages.deploy.DeployResponse;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The {@link HostTcpClient} is a implementation of the CommClient using a TCP
 * end-to-end connection.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class HostTcpClient implements Protocol, AutoCloseable {

    private static final Logger log = Logger.getLogger(HostTcpClient.class);

    private final HostLowLevelTcpClient client;

    public HostTcpClient(final String host, final int port) {
        client = new HostLowLevelTcpClient(host, port);
    }

    @Override
    public LogFilesResponse collectLogFilesOperation(LogFilesRequest request) throws Exception {
        ensureConnection();

        // send the request
        client.sendMessage(request);

        // receive the answer
        Object o = client.receive();

        client.disconnect();

        if (o instanceof LogFilesResponse) {
            log.debug("Logfiles received, now save them");

            return (LogFilesResponse) o;
        } else if (o instanceof CommunicationException) {
            throw (CommunicationException) o;
        } else if (o instanceof Exception) {
            throw (Exception) o;
        } else {
            throw new CommunicationException("Invalid response received: " + o);
        }
    }

    private void ensureConnection() {
        try {
            client.reconnect(Constants.TIMEOUT);
        } catch (IOException e) {
            throw new ConnectionException("could not reconnect", e);
        }
    }

    @Override
    public DeployResponse deployOperation(DeployRequest request) throws Exception {
        ensureConnection();

        client.sendMessage(request);

        // wait for response
        Object o = client.receive();

        client.disconnect();

        if (o instanceof DeployResponse) {
            log.debug("deployment successful");
            return (DeployResponse) o;
        } else if (o instanceof CommunicationException) {
            throw (CommunicationException) o;
        } else if (o instanceof Exception) {
            throw (Exception) o;
        } else {
            throw new CommunicationException("Invalid response received: " + o);
        }
    }


    @Override
    public void close() throws Exception {
        client.close();
    }
}
