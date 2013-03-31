package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import betsy.data.Process;
import betsy.data.engines.bpelg.BpelgEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualBoxController;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualOdeEngine extends VirtualEngine {

	private final BpelgEngine defaultEngine;
	private final Logger log = Logger.getLogger(getClass());
	
	public VirtualOdeEngine(VirtualBoxController vbc) {
		super(vbc);
		this.defaultEngine = new BpelgEngine();
	}
	
    @Override
    public String getName() {
        return "ode_v";
    }

    @Override
	public List<ServiceAddress> getRequiredAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		// TODO adapt to ODE
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
	public String getVMLogfileDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVMDeploymentDir() {
		// TODO Auto-generated method stub
		return null;
	}

}
