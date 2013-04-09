package de.uniba.wiai.dsg.betsy.virtual.host.utils;

import groovy.util.AntBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The {@link VirtualBoxWebService} offers methods to start and stop the
 * VBoxWebSrv executable of VirtualBox. This service is mandatory for the usage
 * of the JAXWS that is used by betsy to control the virtual machines.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualBoxWebService {

	private final AntBuilder ant = new AntBuilder();
	private final Logger log = Logger.getLogger(getClass());
	private final String path;
	private Process vboxServiceProcess;

	public VirtualBoxWebService(final String path) {
		if (StringUtils.isBlank(path)) {
			throw new IllegalArgumentException(
					"Path to the VBoxWebSrv must not be null or empty!");
		}
		this.path = path;
	}

	/**
	 * Start the VBoxWebSrv and wait 5 seconds to assure it is running. The
	 * VBoxWebSrv is started with a disabled authentication library.
	 * 
	 * @throws IOException
	 *             thrown if starting the VBoxWebSrv failed
	 */
	public void start() throws IOException {
		// start VBoxService
		ProcessBuilder pb = new ProcessBuilder(path, "-A", "null");
		vboxServiceProcess = pb.start();
		// give the webSrv some time to start
		log.debug("Waiting 5 seconds for the VBoxWebSrv to start...");

		Map<String, Object> map = new HashMap<>();
		map.put("seconds", 5);
		ant.invokeMethod("sleep", map);

		log.debug("...VBoxWebSrv started!");
	}

	/**
	 * Destroy the process VBoxWebSrv is running in
	 */
	public void stop() {
		log.debug("Stopping VBoxWebSrv...");
		if (vboxServiceProcess != null) {
			vboxServiceProcess.destroy();
		}
	}

}
