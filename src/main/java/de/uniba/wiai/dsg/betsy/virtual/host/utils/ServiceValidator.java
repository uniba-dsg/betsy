package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import betsy.data.engines.Engine;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.InvalidResponseException;
import de.uniba.wiai.dsg.betsy.virtual.host.comm.CommClient;
import de.uniba.wiai.dsg.betsy.virtual.host.comm.TCPCommClient;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.TimeoutException;

/**
 * The {@link ServiceValidator} validates a {@link ServiceAddress} and can
 * therefore determine if an {@link Engine} is ready for usage.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class ServiceValidator {

	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Check whether the {@link Engine} is ready for usage.
	 * 
	 * @param engineServices
	 *            services to verify
	 * @param secondsToWait
	 *            maximum time to wait for the services to become available
	 * @return true if all services are ready, false if not
	 * 
	 * @throws MalformedURLException
	 *             thrown if one of the {@link ServiceAddress} did contain an
	 *             invalid destination.
	 * @throws InterruptedException
	 *             thrown if waiting on the services was interrupted
	 */
	public boolean isEngineReady(final List<ServiceAddress> engineServices,
			final int secondsToWait) throws MalformedURLException,
			InterruptedException {
		if (engineServices.size() <= 0) {
			return true;
		}
		if (secondsToWait <= 0) {
			throw new IllegalArgumentException("secondsToWait must be greater "
					+ "than 0");
		}

		final CountDownLatch cdl = new CountDownLatch(engineServices.size());

		for (final ServiceAddress sa : engineServices) {
			final URL serviceURL = new URL(sa.toString());
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (sa.isRequiringHtmlContent()) {
							waitUntilAvailable(serviceURL,
									sa.getRequiredHtmlContent(),
									secondsToWait * 1000);
						} else {
							waitUntilAvailable(serviceURL, secondsToWait * 1000);
						}
						// available and no exception --> decrement
						cdl.countDown();
					} catch (TimeoutException e) {
						// ignore, service not available and terminate
						// thread
					}
				}
			}).start();
		}

		log.debug("ServiceValidator waiting at max " + secondsToWait
				+ "seconds");
		return cdl.await(secondsToWait, TimeUnit.SECONDS);
	}

	/**
	 * Check whether betsys endpoint is ready for usage.
	 * 
	 * @param timeoutInMs
	 *            maximum time to wait for the server
	 * @return true if the server is available
	 */
	public boolean isBetsyServerReady(final int timeoutInMs) {
		CommClient cc = null;
		try {
			cc = new TCPCommClient("127.0.0.1", 48888);
			cc.reconnect(timeoutInMs);
			return cc.isConnectionAlive();
		} catch (IOException | InvalidResponseException exception) {
			// ignore
			log.debug("Exception in bVMS availability test: ", exception);
		} finally {
			if (cc != null) {
				cc.disconnect();
			}
		}
		return false;
	}

	private void waitUntilAvailable(final URL url, final int timeoutInMs)
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

	private void waitUntilAvailable(final URL url,
			final String requiredContent, final int timeoutInMs)
			throws TimeoutException {
		long start = -System.currentTimeMillis();
		boolean urlAvailable = isAddressAvailable(url, 5000);
		while (!urlAvailable
				&& (start + System.currentTimeMillis()) < timeoutInMs) {
			log.trace("Waiting for address '" + url.toString()
					+ "' to be available");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
			urlAvailable = isAddressAvailable(url, 5000);
		}

		if (!urlAvailable) {
			throw new TimeoutException("URL '" + url.toString()
					+ "' is not available, operation timed out.");
		}

		boolean contentAvailable = isAddressContentAvailable(url,
				requiredContent, 5000);
		while (!contentAvailable
				&& (start + System.currentTimeMillis()) < timeoutInMs) {
			log.trace("Waiting for content of address'" + url.toString()
					+ "' to be available");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
			contentAvailable = isAddressContentAvailable(url, requiredContent,
					5000);
		}

		if (!contentAvailable) {
			throw new TimeoutException("Required content of URL '"
					+ url.toString()
					+ "' is not available, operation timed out.");
		}

	}

	private boolean isAddressAvailable(final URL url, int timeout) {
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

	private boolean isAddressContentAvailable(final URL url,
			final String content, int timeout) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url.toString());
			HttpResponse response = client.execute(request);
			String html = EntityUtils.toString(response.getEntity());
			return html.contains(content);
		} catch (IOException exception) {
			// ignore exception
			return false;
		}
	}

}
