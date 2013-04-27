package betsy.virtual.server.comm;

/**
 * The {@link CommServer} is running on the VirtualMachine and accepts
 * connections from a client.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public interface CommServer {

	/**
	 * Wait and listen until a new connection is established.
	 */
	public void waitForConnection();

	/**
	 * Close the {@link CommServer}.
	 */
	public void close();

}
