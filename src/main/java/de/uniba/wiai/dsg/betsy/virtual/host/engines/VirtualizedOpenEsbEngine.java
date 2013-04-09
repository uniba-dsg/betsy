package de.uniba.wiai.dsg.betsy.virtual.host.engines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import betsy.data.Process;
import betsy.data.engines.openEsb.OpenEsbEngine;
import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.FileMessage;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualizedEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

public class VirtualizedOpenEsbEngine extends VirtualizedEngine {

	private final Configuration config = Configuration.getInstance();
	private final OpenEsbEngine defaultEngine;

	public VirtualizedOpenEsbEngine() {
		super();
		this.defaultEngine = new OpenEsbEngine();
	}

	@Override
	public String getName() {
		return "openesb_v";
	}

	@Override
	public List<ServiceAddress> getVerifiableServiceAddresses() {
		List<ServiceAddress> saList = new LinkedList<>();
		saList.add(new ServiceAddress("http://localhost:8383/"));
		saList.add(new ServiceAddress("http://localhost:4848/"));
		return saList;
	}

	@Override
	public Set<Integer> getRequiredPorts() {
		Set<Integer> portList = new HashSet<>();
		// BPEL services
		portList.add(18181);
		// HTTP
		portList.add(8383);
		// HTTPS
		portList.add(8384);
		// ADMIN
		portList.add(4848);
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
	public DeployOperation buildDeployOperation(Process process)
			throws IOException {
		Path path = getDeployableFilePath(process);
		Path filenamePath = path.getFileName();
		String filename = filenamePath.toString();
		byte[] data = Files.readAllBytes(path);

		DeployOperation operation = new DeployOperation();
		FileMessage fm = new FileMessage(filename, data);
		operation.setFileMessage(fm);
		operation.setEngineName(getName());
		operation.setBpelFileNameWithoutExtension(process
				.getBpelFileNameWithoutExtension());
		operation.setEngineLogDir(getVMLogfileDir());
		operation.setDeploymentFile(getVMDeploymentFile());
		operation.setDeployTimeout(getVMDeploymentTimeout());

		return operation;
	}

	@Override
	public String getVMDeploymentDir() {
		// no deployment dir, openesb requires deployment executable
		return null;
	}

	@Override
	public String getVMLogfileDir() {
		return config.getValueAsString(
				"virtualisation.engines.openesb_v.logfileDir",
				"/opt/openesb/glassfish/domains/domain1/logs");
	}

	public String getVMDeploymentFile() {
		return config.getValueAsString(
				"virtualisation.engines.openesb_v.deploymentExecutable",
				"/opt/openesb/glassfish/bin/asadmin");
	}

	@Override
	public Path getDeployableFilePath(Process process) {
		return Paths.get(process.getTargetPackageCompositeFilePath());
	}

}
