package betsy.virtual.host;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import betsy.data.BetsyProcess;
import betsy.virtual.common.messages.DeployOperation;

/**
 * The {@link VirtualizedEngineAPI} offers methods that are required if dealing
 * with virtualized testing using betsy.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public interface VirtualizedEngineAPI {

	/**
	 * Get the name of the VirtualMachine that is used by this engines.<br>
	 * The name shall be the prefix 'betsy-' concatenated with the name of the
	 * virtualized engine.
	 * 
	 * @return name of the engine's VirtualMachine
	 */
	public String getVirtualMachineName();

	/**
	 * Get every {@link ServiceAddress} that must be available for the engine
	 * before testing it.
	 * 
	 * @return the list of required services for the engine
	 */
	public List<ServiceAddress> getVerifiableServiceAddresses();

	/**
	 * Get all ports that are required by the engine to be testable.
	 * 
	 * @return list of required ports
	 */
	public Set<Integer> getRequiredPorts();

	/**
	 * Build the {@link DeployOperation} for the given process.
	 * 
	 * @param process
	 *            process to create the {@link DeployOperation} for
	 * @return the created deployOperation
	 * @throws IOException
	 *             thrown if the files of the process could not be read
	 */
	public DeployOperation buildDeployOperation(BetsyProcess process)
			throws IOException;

	/**
	 * Get the directory where the logfiles are stored on the VirtualMachine.
	 * 
	 * @return directory of the logfiles on the VM
	 */
	public String getVMLogfileDir();

	/**
	 * Get the directory where the bVMS is installed on the VirtualMachine.
	 * 
	 * @return directory of the bVMS on the VM
	 */
	public String getVMbVMSDir();

	/**
	 * Get the directory where the files have to be deployed to on the
	 * VirtualMachine
	 * 
	 * @return directory where the files have to be deployed to on the
	 *         VirtualMachine
	 */
	public String getVMDeploymentDir();

	/**
	 * Get the maximum time to wait on the deployment.
	 * 
	 * @return max time to wait on process deployment
	 */
	public Integer getVMDeploymentTimeout();

	/**
	 * Get the path to the deployable file of the given process.
	 * 
	 * @param process
	 *            process to get deployable file path for
	 * @return path to the deployable file of the given process.
	 */
	public Path getDeployableFilePath(BetsyProcess process);

}
