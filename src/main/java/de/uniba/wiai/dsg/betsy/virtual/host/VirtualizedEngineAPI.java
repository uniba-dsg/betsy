package de.uniba.wiai.dsg.betsy.virtual.host;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;

import betsy.data.Process;

// TODO JavaDoc
public interface VirtualizedEngineAPI {

	public String getVirtualMachineName();

	public boolean isVirtualMachineReady();

	public boolean isVMImported();

	// TODO pronounce verifiable service
	public List<ServiceAddress> getRequiredAddresses();

	// TODO change to enginePorts
	public Set<Integer> getRequiredPorts();

	// required to forwared the port
	public Integer getEndpointPort();

	// TODO
	public String getEndpointPath(Process process);

	public DeployOperation buildDeployContainer(Process process)
			throws IOException;

	public String getVMLogfileDir();

	public String getVMDeploymentDir();

	public Integer getVMDeploymentTimeout();

	public String getTargetPackageExtension();

}
