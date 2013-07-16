package betsy.virtual.host.engines;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.Configuration;
import betsy.data.BetsyProcess;
import betsy.data.engines.activeBpel.ActiveBpelEngine;
import betsy.virtual.host.ServiceAddress;

public class VirtualizedActiveBpelEngine extends VirtualizedEngine {

	private final ActiveBpelEngine defaultEngine;
	private final Configuration config = Configuration.getInstance();

	public VirtualizedActiveBpelEngine() {
		super();
		this.defaultEngine = new ActiveBpelEngine();
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
	public String getEndpointUrl(BetsyProcess process) {
		// is not delegated because of the dependency to the local Tomcat
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
	public void buildArchives(BetsyProcess process) {
		// use default engine's operations
		defaultEngine.buildArchives(process);
	}

	@Override
	public String getXsltPath() {
		return defaultEngine.getXsltPath();
	}

	@Override
	public void onPostDeployment(BetsyProcess process) {
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
	public Path getDeployableFilePath(BetsyProcess process) {
		return Paths.get(process.getTargetPackageFilePath("bpr"));
	}

	@Override
	public String getVMbVMSDir() {
		String bVMSDir = config.getValueAsString(
				"virtualisation.engines.active-bpel_v.bvmsDir", "/opt/betsy/");
		bVMSDir = bVMSDir.endsWith("/") ? bVMSDir : bVMSDir + "/";
		bVMSDir += "log";
		return bVMSDir;
	}
}
