package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.data.Process;
import betsy.data.engines.openEsb.OpenEsbEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualBoxController;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualOpenEsbEngine extends VirtualEngine {

	private final OpenEsbEngine defaultEngine;

	public VirtualOpenEsbEngine(VirtualBoxController vbc) {
		super(vbc);
		this.defaultEngine = new OpenEsbEngine();
		this.defaultEngine.setPackageBuilder(new VirtualEnginePackageBuilder());
	}

	@Override
	public String getName() {
		return "openesb_v";
	}

	@Override
	public List<ServiceAddress> getVerifiableServiceAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		// TODO adapt
		saList.add(new ServiceAddress("http://localhost:18181/"));
		return saList;
	}

	@Override
	public Set<Integer> getRequiredPorts() {
		Set<Integer> portList = new HashSet<>();
		portList.add(18181);
		return portList;
	}

	@Override
	public String getEndpointUrl(Process process) {
		return "http://localhost:18181/"
				+ process.getBpelFileNameWithoutExtension() + "TestInterface";
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
