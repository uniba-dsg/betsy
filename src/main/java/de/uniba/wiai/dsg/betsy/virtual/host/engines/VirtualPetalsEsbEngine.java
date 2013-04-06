package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.data.Process;
import betsy.data.engines.petalsEsb.PetalsEsbEngine;
import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualBoxController;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualEnginePackageBuilder;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualPetalsEsbEngine extends VirtualEngine {

	private final Configuration config = Configuration.getInstance();
	private final PetalsEsbEngine defaultEngine;

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
	public List<ServiceAddress> getVerifiableServiceAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		saList.add(new ServiceAddress(
				"http://localhost:8084/petals/services/listServices"));
		return saList;
	}

	@Override
	public Set<Integer> getRequiredPorts() {
		Set<Integer> portList = new HashSet<>();
		portList.add(8084);
		return portList;
	}

	@Override
	public String getEndpointUrl(Process process) {
		return "http://localhost:8084/petals/services/"
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
				"virtualisation.engines.petalsesb_v.deploymentDir",
				"/opt/petalsesb/install");
	}

	@Override
	public String getVMLogfileDir() {
		return config.getValueAsString(
				"virtualisation.engines.petalsesb_v.logfileDir",
				"/opt/petalsesb/logs");
	}

	@Override
	public Path getDeployableFilePath(Process process) {
		return Paths.get(process.getTargetPackageCompositeFilePath());
	}
}
