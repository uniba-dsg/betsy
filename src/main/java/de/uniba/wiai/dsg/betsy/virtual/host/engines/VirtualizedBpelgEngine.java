package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.data.Process;
import betsy.data.engines.bpelg.BpelgEngine;
import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualizedEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualizedBpelgEngine extends VirtualizedEngine {

	private final Configuration config = Configuration.getInstance();
	private final BpelgEngine defaultEngine;

	public VirtualizedBpelgEngine() {
		super();
		this.defaultEngine = new BpelgEngine();
	}

	@Override
	public String getName() {
		return "bpelg_v";
	}

	@Override
	public List<ServiceAddress> getVerifiableServiceAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		saList.add(new ServiceAddress("http://localhost:8080/bpel-g/services"));
		return saList;
	}

	@Override
	public Set<Integer> getRequiredPorts() {
		Set<Integer> portList = new HashSet<>();
		portList.add(8080);
		return portList;
	}

	@Override
	public String getEndpointUrl(Process process) {
		return "http://localhost:8080/bpel-g/services/"
				+ process.getBpelFileNameWithoutExtension()
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
	public Path getDeployableFilePath(Process process) {
		return Paths.get(process.getTargetPackageFilePath("zip"));
	}
}
