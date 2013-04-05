package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedPetalsEsbDeployer implements VirtualizedEngineDeployer {

	@Override
	public String getName() {
		return "petalsesb_v";
	}

	@Override
	public void deploy(DeployOperation container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostDeployment(DeployOperation container)
			throws DeployException {
		// TODO Auto-generated method stub
		
	}

}
