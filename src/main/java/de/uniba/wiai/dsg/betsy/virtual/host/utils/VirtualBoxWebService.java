package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

public class VirtualBoxWebService {

	private final Logger log = Logger.getLogger(getClass());
	private final String path;
	private Process vboxServiceProcess;
	
	public VirtualBoxWebService(final String path) {
		if(path == null || path.trim().isEmpty()) {
			throw new IllegalArgumentException("Path to the VBoxWebSrv must not be null or empty!");
		}
		this.path = path;
	}
	
	public void start() throws IOException {
		// start VBoxService
		ProcessBuilder pb = new ProcessBuilder(path);
		vboxServiceProcess = pb.start();
		// give the webSrv some time to start
		log.debug("Waiting 3 seconds for the VBoxWebSrv to start...");
		long start = -System.currentTimeMillis();
		while(start + System.currentTimeMillis() < 3000) {
			try {
				Thread.sleep(1000);
			}catch(Exception exception) {
				// ignore
				log.warn("...interrupted sleep");
			}
		}
		log.debug("...VBoxWebSrv started!");
	}
	
	public void stop() {
		log.debug("Stopping VBoxWebSrv...");
		if(vboxServiceProcess != null) {
			vboxServiceProcess.destroy();
		}
	}
	
}
