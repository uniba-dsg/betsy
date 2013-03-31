package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Collection;

import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.PortUsageException;

public class PortVerifier {

	public boolean isPortAvailable(final int portNumber) {
		ServerSocket serverSocket = null;
		DatagramSocket datagramSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
			serverSocket.setReuseAddress(true);
			datagramSocket = new DatagramSocket(portNumber);
			datagramSocket.setReuseAddress(true);
			// both could be creates, port is unused!
			return true;
		} catch (final IOException e) {
			return false;
		} finally {
			if (datagramSocket != null) {
				datagramSocket.close();
			}
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (final IOException e) {
					// ignore
				}
			}
		}
	}

	public void verify(Collection<Integer> ports) throws PortUsageException {
		for (Integer port : ports) {
			if (!this.isPortAvailable(port)) {
				throw new PortUsageException("Can't forward port '" + port
						+ "'. The port is already in use by another"
						+ " appliaction.");
			}
		}
	}

}
