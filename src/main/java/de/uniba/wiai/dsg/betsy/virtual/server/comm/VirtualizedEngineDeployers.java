package de.uniba.wiai.dsg.betsy.virtual.server.comm;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.uniba.wiai.dsg.betsy.virtual.server.deployers.VirtualizedActiveBpelDeployer;
import de.uniba.wiai.dsg.betsy.virtual.server.deployers.VirtualizedBpelgDeployer;
import de.uniba.wiai.dsg.betsy.virtual.server.deployers.VirtualizedOdeDeployer;
import de.uniba.wiai.dsg.betsy.virtual.server.deployers.VirtualizedOpenEsbDeployer;
import de.uniba.wiai.dsg.betsy.virtual.server.deployers.VirtualizedOrchestraDeployer;
import de.uniba.wiai.dsg.betsy.virtual.server.deployers.VirtualizedPetalsEsbDeployer;

public class VirtualizedEngineDeployers {

	/**
	 * Returns a list of all available deployable engines.
	 * 
	 * @return a list of all available deployable engines
	 */
	public static List<VirtualizedEngineDeployer> availableEngines() {
		LinkedList<VirtualizedEngineDeployer> deployers = new LinkedList<>();
		deployers.add(new VirtualizedActiveBpelDeployer());
		deployers.add(new VirtualizedBpelgDeployer());
		deployers.add(new VirtualizedOdeDeployer());
		deployers.add(new VirtualizedOrchestraDeployer());
		deployers.add(new VirtualizedOpenEsbDeployer());
		deployers.add(new VirtualizedPetalsEsbDeployer());
		return deployers;
	}

	/**
	 * Find a virtualized engine by name.
	 * 
	 * @param name
	 *            the name of a virtualized engine
	 * @return the virtualized engine if it can be found
	 * @throws IllegalArgumentException
	 *             if the virtualized engine can not be found
	 */
	public static VirtualizedEngineDeployer build(final String name) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("name must not be null or empty");
		}
		String namet = name.trim();

		for (VirtualizedEngineDeployer dep : availableEngines()) {
			if (dep.getName().equals(namet)) {
				return dep;
			}
		}

		// no engine with matching name found
		throw new IllegalArgumentException("passed deployer " + namet
				+ " does not exist");
	}

	public static List<VirtualizedEngineDeployer> build(List<String> names) {
		List<VirtualizedEngineDeployer> deployers = new LinkedList<>();
		for (String name : names) {
			deployers.add(VirtualizedEngineDeployers.build(name));
		}
		return deployers;
	}

}
