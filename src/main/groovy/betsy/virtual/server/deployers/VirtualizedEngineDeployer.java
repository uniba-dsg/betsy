package betsy.virtual.server.deployers;

import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.DeployOperation;

/**
 * A {@link VirtualizedEngineDeployer} deploys a process that was received in a
 * {@link DeployOperation} to the Deployers engine.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public interface VirtualizedEngineDeployer {

	/**
	 * Get the name of the {@link VirtualizedEngineDeployer}. This name must be
	 * identical with the name of the engine.
	 * 
	 * @return name of the deployer
	 */
	public String getName();

	/**
	 * Deploy the operation to the engine.
	 * 
	 * @param operation
	 *            to deploy
	 * @throws DeployException
	 *             thrown if deployment failed
	 */
	public void deploy(DeployOperation operation) throws DeployException;

	/**
	 * Invoked after deployment of the process. Should not return before the
	 * Process is ready for usage.
	 * 
	 * @param operation
	 *            processes deployment operation
	 * @throws DeployException
	 *             thrown if process was not available before timeout
	 */
	public void onPostDeployment(DeployOperation operation)
			throws DeployException;

}
