package betsy.virtual.host.engines;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.Configuration;
import betsy.data.Process;
import betsy.data.engines.ode.OdeEngine;
import betsy.virtual.host.VirtualizedEngine;
import betsy.virtual.host.utils.ServiceAddress;

public class VirtualizedOdeEngine extends VirtualizedEngine {

	private final Configuration config = Configuration.getInstance();
	private final OdeEngine defaultEngine;

	public VirtualizedOdeEngine() {
		super();
		this.defaultEngine = new OdeEngine();
	}

	@Override
	public String getName() {
		return "ode_v";
	}

	@Override
	public List<ServiceAddress> getVerifiableServiceAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		saList.add(new ServiceAddress("http://localhost:8080/ode"));
		return saList;
	}

	@Override
	public String getEndpointUrl(Process process) {
		return "http://localhost:8080/ode/processes/"
				+ process.getBpelFileNameWithoutExtension() + "TestInterface";
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
				"virtualisation.engines.ode_v.deploymentDir",
				"/usr/share/tomcat7/webapps/ode/WEB-INF/processes");
	}

	@Override
	public String getVMLogfileDir() {
		return config.getValueAsString(
				"virtualisation.engines.ode_v.logfileDir",
				"/var/lib/tomcat7/logs");
	}

	@Override
	public Path getDeployableFilePath(Process process) {
		return Paths.get(process.getTargetPackageFilePath("zip"));
	}
	
	@Override
	public String getVMbVMSDir() {
		String bVMSDir = config.getValueAsString(
				"virtualisation.engines.ode_v.bvmsDir",
				"/opt/betsy/");
		bVMSDir = bVMSDir.endsWith("/") ? bVMSDir : bVMSDir + "/";
		bVMSDir += "log";
		return bVMSDir;
	}
}
