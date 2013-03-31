package de.uniba.wiai.dsg.betsy.virtual.host;

import groovy.util.AntBuilder;

import java.util.HashMap;
import java.util.Map;

import de.uniba.wiai.dsg.betsy.Configuration;

import betsy.data.EnginePackageBuilder;
import betsy.data.Process;


public class VirtualEnginePackageBuilder extends EnginePackageBuilder {

	private final AntBuilder ant = new AntBuilder();
	private final Configuration config = Configuration.getInstance();

	public VirtualEnginePackageBuilder(final String engineName) {
		super(engineName);
	}

	@Override
	public void replacePartnerTokenWithValue(Process process) {
		String hostIp = config.getValueAsString("virtualisation.partnerIp", "10.0.2.2");
		String ipPort = config.getValueAsString("PARTNER_IP_AND_PORT");
		String port = ipPort.split(":")[1];

		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("message", "Setting Partner Address for "+process.toString()+" on "+process.getEngine().toString()+" to "+hostIp + ":" + port);
		ant.invokeMethod("echo", messageMap);

		Map<String, Object> replaceMap = new HashMap<>();
		replaceMap.put("dir", process.getTargetBpelPath());
		replaceMap.put("token", "PARTNER_IP_AND_PORT");
		replaceMap.put("value", hostIp + ":" + port);

		ant.invokeMethod("replace", replaceMap);
	}
}
