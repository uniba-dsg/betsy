package betsy.virtual.host.virtualbox.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Collection;

import betsy.virtual.host.exceptions.PortUsageException;

/**
 * The {@link PortVerifier} offers methods to verify if a port is free or
 * already in use.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class PortVerifier {

	private static void failForUnreachablePort(final int portNumber) throws PortUsageException{
		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				DatagramSocket datagramSocket = new DatagramSocket(portNumber)) {

			serverSocket.setReuseAddress(true);
			datagramSocket.setReuseAddress(true);
			// both could be creates, port is unused!
		} catch (final IOException e) {
            throw new PortUsageException("Can't forward port '" + portNumber
                    + "'. The port is already in use by another"
                    + " application.", e);
		}
	}

	/**
	 * Verify a {@link Collection} of ports. If any of these ports is already un
	 * use, an exception is thrown.
	 * 
	 * @param ports
	 *            ports to check for availability
	 * @throws PortUsageException
	 *             thrown if any port is already used
	 */
	public static void failForUsedPorts(Collection<Integer> ports)
			throws PortUsageException {
		for (Integer port : ports) {
			failForUnreachablePort(port);
		}
	}

}
