package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedOrchestraDeployer implements VirtualizedEngineDeployer {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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
