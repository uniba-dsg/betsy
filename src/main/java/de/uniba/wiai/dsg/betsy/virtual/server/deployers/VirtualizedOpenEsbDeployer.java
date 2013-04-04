package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedOpenEsbDeployer implements VirtualizedEngineDeployer {

	@Override
	public String getName() {
		return "openesb_v";
	}

	@Override
	public void deploy(DeployContainer container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostDeployment(DeployContainer container)
			throws DeployException {
		// TODO Auto-generated method stub
		
	}


}
