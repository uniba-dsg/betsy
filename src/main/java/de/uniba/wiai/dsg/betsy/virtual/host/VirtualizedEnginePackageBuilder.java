package de.uniba.wiai.dsg.betsy.virtual.host;

import groovy.util.AntBuilder;

import java.util.HashMap;
import java.util.Map;

import betsy.data.Process;
import betsy.data.engines.EnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.Configuration;

/**
 * The {@link VirtualizedEnginePackageBuilder} extends the classic
 * {@link EnginePackageBuilder} and replaces the partner IP and port with the
 * host IP and port specified in the config for the virtualized testing.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedEnginePackageBuilder extends EnginePackageBuilder {

	private final AntBuilder ant = new AntBuilder();
	private final Configuration config = Configuration.getInstance();

	public VirtualizedEnginePackageBuilder() {
		super();
	}

	@Override
	public void replacePartnerTokenWithValue(Process process) {
		String hostIp = config.getValueAsString("virtualisation.partnerIp",
				"10.0.2.2");
		String hostPort = config.getValueAsString("virtualisation.partnerPort",
				"2000");

		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("message",
				"Setting Partner Address for " + process.toString() + " on "
						+ process.getEngine().toString() + " to " + hostIp
						+ ":" + hostPort);
		ant.invokeMethod("echo", messageMap);

		Map<String, Object> replaceMap = new HashMap<>();
		replaceMap.put("dir", process.getTargetBpelPath());
		replaceMap.put("token", "PARTNER_IP_AND_PORT");
		replaceMap.put("value", hostIp + ":" + hostPort);

		ant.invokeMethod("replace", replaceMap);
	}
}
