package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.data.Process;
import betsy.data.engines.ode.OdeEngine;
import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualBoxController;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualOdeEngine extends VirtualEngine {

	private final Configuration config = Configuration.getInstance();
	private final OdeEngine defaultEngine;
	
	public VirtualOdeEngine(VirtualBoxController vbc) {
		super(vbc);
		this.defaultEngine = new OdeEngine();
		this.defaultEngine.setPackageBuilder(new VirtualEnginePackageBuilder());
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
		return "/ode/processes/" + process.getBpelFileNameWithoutExtension()
				+ "TestInterface";
	}

	@Override
	public void buildArchives(Process process) {
		// use default engine's operations
		defaultEngine.buildArchives(process);
	}
	
	@Override
    public String getXsltPath() {
        return "src/main/xslt/"+defaultEngine.getName();
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
	public String getTargetPackageExtension() {
		return "zip";
	}
}
