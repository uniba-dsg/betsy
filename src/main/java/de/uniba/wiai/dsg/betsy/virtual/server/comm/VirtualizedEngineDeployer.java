package de.uniba.wiai.dsg.betsy.virtual.server.comm;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;

public interface VirtualizedEngineDeployer {

	public String getName();
	
	public void deploy(DeployContainer container) throws DeployException;
	
	public void onPostDeployment(DeployContainer container) throws DeployException;
	
}
