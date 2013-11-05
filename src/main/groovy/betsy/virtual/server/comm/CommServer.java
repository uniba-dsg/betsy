package betsy.virtual.server.comm;

/**
 * The {@link CommServer} is running on the VirtualMachine and accepts
 * connections from a client.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public interface CommServer extends AutoCloseable {

    /**
     * Wait and listen until a new connection is established.
     */
    public void handleNextConnection();

}
