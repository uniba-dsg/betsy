package betsy.virtual.host.engines;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.Configuration;
import betsy.data.Process;
import betsy.data.engines.orchestra.OrchestraEngine;
import betsy.virtual.host.VirtualizedEngine;
import betsy.virtual.host.utils.ServiceAddress;

public class VirtualizedOrchestraEngine extends VirtualizedEngine {

	private final OrchestraEngine defaultEngine;
	private Configuration config = Configuration.getInstance();

	public VirtualizedOrchestraEngine() {
		super();
		this.defaultEngine = new OrchestraEngine();
	}

	@Override
	public String getName() {
		return "orchestra_v";
	}

	@Override
	public List<ServiceAddress> getVerifiableServiceAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		saList.add(new ServiceAddress("http://localhost:8080/orchestra"));
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
		// is not delegated because of the dependency to the local Tomcat
		return "http://localhost:8080/orchestra/"
				+ process.getBpelFileNameWithoutExtension() + "TestInterface";
	}

	@Override
	public void buildArchives(Process process) {
		// use default engine's operations
		defaultEngine.buildArchives(process);
	}

	@Override
	public String getXsltPath() {
		return defaultEngine.getXsltPath();
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
	public String getVMLogfileDir() {
		return config.getValueAsString(
				"virtualisation.engines.orchestra_v.logfileDir",
				"/var/lib/tomcat7/logs");
	}

	@Override
	public String getVMDeploymentDir() {
		return config.getValueAsString(
				"virtualisation.engines.orchestra_v.deploymentDir",
				"/home/betsy/orchestra-cxf-tomcat");
	}

	@Override
	public Path getDeployableFilePath(Process process) {
		return Paths.get(process.getTargetPackageFilePath("zip"));
	}
	
	@Override
	public String getVMbVMSDir() {
		String bVMSDir = config.getValueAsString(
				"virtualisation.engines.orchestra_v.bvmsDir",
				"/opt/betsy/");
		bVMSDir = bVMSDir.endsWith("/") ? bVMSDir : bVMSDir + "/";
		bVMSDir += "log";
		return bVMSDir;
	}
}
