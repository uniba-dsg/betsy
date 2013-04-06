package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.data.Process;
import betsy.data.engines.activeBpel.ActiveBpelEngine;
import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualBoxController;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualActiveBpelEngine extends VirtualEngine {

	private final ActiveBpelEngine defaultEngine;
	private final Configuration config = Configuration.getInstance();

	public VirtualActiveBpelEngine(VirtualBoxController vbc) {
		super(vbc);
		this.defaultEngine = new ActiveBpelEngine();
		this.defaultEngine.setPackageBuilder(new VirtualEnginePackageBuilder());
	}

	@Override
	public String getName() {
		return "active_bpel_v";
	}

	@Override
	public List<ServiceAddress> getVerifiableServiceAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		saList.add(new ServiceAddress(
				"http://localhost:8080/active-bpel/services"));
		saList.add(new ServiceAddress("http://localhost:8080/BpelAdmin/",
				"Running"));
		return saList;
	}

	@Override
	public String getEndpointUrl(Process process) {
		return "http://localhost:8080/active-bpel/services/"
				+ process.getBpelFileNameWithoutExtension()
				+ "TestInterfaceService";
	}

	@Override
	public Set<Integer> getRequiredPorts() {
		Set<Integer> portList = new HashSet<>();
		portList.add(8080);
		return portList;
	}

	@Override
	public void buildArchives(Process process) {
		// use default engine's operations
		defaultEngine.buildArchives(process);
	}

	@Override
	public String getXsltPath() {
		return "src/main/xslt/" + defaultEngine.getName();
	}

	@Override
	public void onPostDeployment() {
		// not required. deploy is in sync and does not return before engine is
		// deployed
	}

	@Override
	public void onPostDeployment(Process process) {
		// not required. deploy is in sync and does not return before process is
		// deployed
	}

	@Override
	public String getVMDeploymentDir() {
		return config.getValueAsString(
				"virtualisation.engines.active-bpel_v.deploymentDir",
				"/usr/share/tomcat5.5/bpr");
	}

	@Override
	public String getVMLogfileDir() {
		return config.getValueAsString(
				"virtualisation.engines.active-bpel_v.logfileDir",
				"/usr/share/tomcat5.5/logs");
	}

	@Override
	public String getTargetPackageExtension() {
		return "bpr";
	}
}
