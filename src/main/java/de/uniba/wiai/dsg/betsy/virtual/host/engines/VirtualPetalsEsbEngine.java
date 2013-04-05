package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import betsy.data.Process;
import betsy.data.engines.petalsEsb.PetalsEsbEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualBoxController;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualPetalsEsbEngine extends VirtualEngine {

	private final PetalsEsbEngine defaultEngine;
	private final Logger log = Logger.getLogger(getClass());
    		
	public VirtualPetalsEsbEngine(VirtualBoxController vbc) {
		super(vbc);
		this.defaultEngine = new PetalsEsbEngine();
		this.defaultEngine.setPackageBuilder(new VirtualEnginePackageBuilder());
	}

    @Override
    public String getName() {
        return "petalsesb_v";
    }

    @Override
	public List<ServiceAddress> getRequiredAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		// TODO adapt to petals
		saList.add(new ServiceAddress("http", "localhost", "/petals/services",
				8084));
		return saList;
	}

	@Override
	public Set<Integer> getRequiredPorts() {
		Set<Integer> portList = new HashSet<>();
		portList.add(8084);
		return portList;
	}

	@Override
	public Integer getEndpointPort() {
		return 8084;
	}

	@Override
	public String getEndpointPath(Process process) {
		return "/petals/services/" + process.getBpelFileNameWithoutExtension()
				+ "TestInterfaceService";
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

	@Override
	public String getTargetPackageExtension() {
		// TODO Auto-generated method stub
		return null;
	}

}
