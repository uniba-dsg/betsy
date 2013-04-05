package de.uniba.wiai.dsg.betsy.virtual.host;

import groovy.util.AntBuilder;

import java.util.HashMap;
import java.util.Map;

import betsy.data.Process;
import betsy.data.engines.EnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.Configuration;

public class VirtualEnginePackageBuilder extends EnginePackageBuilder {

	private final AntBuilder ant = new AntBuilder();
	private final Configuration config = Configuration.getInstance();

	public VirtualEnginePackageBuilder() {
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
