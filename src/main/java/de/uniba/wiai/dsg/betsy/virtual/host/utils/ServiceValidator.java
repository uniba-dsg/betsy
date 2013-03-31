package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.comm.CommClient;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.InvalidResponseException;
import de.uniba.wiai.dsg.betsy.virtual.host.comm.TCPCommClient;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.TimeoutException;

public class ServiceValidator {

	private final Logger log = Logger.getLogger(getClass());

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

		for (ServiceAddress sa : engineServices) {
			final URL serviceURL = new URL(sa.toString());
			new Thread(new Runnable() {
				@Override
				public void run() {
					URLPinger pinger = new URLPinger();
					try {
						pinger.waitUntilAvailable(serviceURL,
								secondsToWait * 1000);
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

}
