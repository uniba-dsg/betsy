package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Collection;

import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.PortUsageException;

/**
 * The {@link PortVerifier} offers methods to verify if a port is free or
 * already in use.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class PortVerifier {

	private static boolean isPortAvailable(final int portNumber) {
		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				DatagramSocket datagramSocket = new DatagramSocket(portNumber)) {

			serverSocket.setReuseAddress(true);
			datagramSocket.setReuseAddress(true);
			// both could be creates, port is unused!
			return true;
		} catch (final IOException e) {
			return false;
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
	public static void verify(Collection<Integer> ports)
			throws PortUsageException {
		for (Integer port : ports) {
			if (!isPortAvailable(port)) {
				throw new PortUsageException("Can't forward port '" + port
						+ "'. The port is already in use by another"
						+ " appliaction.");
			}
		}
	}

}