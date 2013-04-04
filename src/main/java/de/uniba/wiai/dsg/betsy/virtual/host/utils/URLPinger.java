package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.TimeoutException;

// TODO new name, refactor
public class URLPinger {

	private Logger log = Logger.getLogger(getClass());

	// TODO As seen on:
	// http://stackoverflow.com/questions/875467/java-client-certificates-over-https-ssl

	private TrustManager[] getNullTrustManager() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };
		return trustAllCerts;
	}

	private HostnameVerifier getNullHostnameVerifier() {
		// Ignore differences between given hostname and certificate hostname
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		return hv;
	}

	public boolean isAddressAvailable(final URL url, int timeout) {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, getNullTrustManager(), new SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(getNullHostnameVerifier());
		} catch (KeyManagementException | NoSuchAlgorithmException exception) {
			// ignore
			log.warn("Exception while setting SSL TrustManager:", exception);
		}

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

	public boolean isAddressAvailable(final URL url) {
		return isAddressAvailable(url, 5000);
	}

	public void waitUntilAvailable(final URL url, final int timeoutInMs)
			throws TimeoutException {
		long start = -System.currentTimeMillis();
		boolean available = isAddressAvailable(url);
		while (!available && (start + System.currentTimeMillis()) < timeoutInMs) {
			log.trace("Waiting for address '" + url.toString()
					+ "' to be available");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
			available = isAddressAvailable(url);
		}

		if (!available) {
			throw new TimeoutException("URL '" + url.toString()
					+ "' is not available, operation timed out.");
		}
	}

}
