package de.uniba.wiai.dsg.betsy.virtual.server.comm;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;

public interface VirtualizedEngineDeployer {

	public String getName();
	
	public void deploy(DeployOperation container) throws DeployException;
	
	public void onPostDeployment(DeployOperation container) throws DeployException;
	
}
