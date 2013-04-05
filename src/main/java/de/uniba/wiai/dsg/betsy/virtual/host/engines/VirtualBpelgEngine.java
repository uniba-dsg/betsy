package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.data.Process;
import betsy.data.engines.bpelg.BpelgEngine;
import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualBoxController;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualBpelgEngine extends VirtualEngine {

	private final Configuration config = Configuration.getInstance();
	private final BpelgEngine defaultEngine;

	public VirtualBpelgEngine(VirtualBoxController vbc) {
		super(vbc);
		this.defaultEngine = new BpelgEngine();
		this.defaultEngine.setPackageBuilder(new VirtualEnginePackageBuilder());
	}

	@Override
	public String getName() {
		return "bpelg_v";
	}

	@Override
	public List<ServiceAddress> getRequiredAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		saList.add(new ServiceAddress("http", "localhost", "/bpel-g/services",
				8080));
		return saList;
	}

	@Override
	public Set<Integer> getRequiredPorts() {
		Set<Integer> portList = new HashSet<>();
		portList.add(8080);
		return portList;
	}

	@Override
	public Integer getEndpointPort() {
		return 8080;
	}

	@Override
	public String getEndpointPath(Process process) {
		return "/bpel-g/services/" + process.getBpelFileNameWithoutExtension()
				+ "TestInterfaceService";
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
				"virtualisation.engines.bpelg_v.deploymentDir",
				"/usr/share/tomcat7/bpr");
	}

	@Override
	public String getVMLogfileDir() {
		return config.getValueAsString(
				"virtualisation.engines.bpelg_v.logfileDir",
				"/var/lib/tomcat7/logs");
	}
	
	@Override
	public String getTargetPackageExtension() {
		return "zip";
	}
}
