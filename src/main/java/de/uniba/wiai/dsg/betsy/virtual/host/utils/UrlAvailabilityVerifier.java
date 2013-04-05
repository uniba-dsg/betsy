package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.TimeoutException;

public class UrlAvailabilityVerifier {

	private static Logger log = Logger.getLogger(UrlAvailabilityVerifier.class);

	public static boolean isAddressAvailable(final URL url, int timeout) {
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			int serverResponseCode = conn.getResponseCode();
			return (200 <= serverResponseCode && serverResponseCode <= 399);
		} catch (IOException exception) {
			// ignore exception
			return false;
		}
	}

	public static void waitUntilAvailable(final URL url, final int timeoutInMs)
			throws TimeoutException {
		long start = -System.currentTimeMillis();
		boolean available = isAddressAvailable(url, 5000);
		while (!available && (start + System.currentTimeMillis()) < timeoutInMs) {
			log.trace("Waiting for address '" + url.toString()
					+ "' to be available");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
			available = isAddressAvailable(url, 5000);
		}

		if (!available) {
			throw new TimeoutException("URL '" + url.toString()
					+ "' is not available, operation timed out.");
		}
	}

}
