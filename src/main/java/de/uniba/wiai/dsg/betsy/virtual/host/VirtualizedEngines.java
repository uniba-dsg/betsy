package de.uniba.wiai.dsg.betsy.virtual.host;

import java.util.LinkedList;
import java.util.List;

import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualActiveBpelEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualBpelgEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualOdeEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualOpenEsbEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualOrchestraEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualPetalsEsbEngine;

public class VirtualizedEngines {

	/**
	 * Returns a list of all available virtualized engines.
	 * 
	 * @return a list of all available virtualized engines
	 */
	public static List<VirtualEngine> availableEngines() {
		VirtualBoxController vbc = new VirtualBoxController();
		vbc.init();

		LinkedList<VirtualEngine> engines = new LinkedList<>();
		engines.add(new VirtualOdeEngine(vbc));
		engines.add(new VirtualBpelgEngine(vbc));
		engines.add(new VirtualOpenEsbEngine(vbc));
		engines.add(new VirtualPetalsEsbEngine(vbc));
		engines.add(new VirtualOrchestraEngine(vbc));
		engines.add(new VirtualActiveBpelEngine(vbc));

		return engines;
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
	public static VirtualEngine build(final String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("name must not be null or empty");
		}
		String namet = name.trim();

		for (VirtualEngine ve : availableEngines()) {
			if (ve.getName().equals(namet)) {
				return ve;
			}
		}

		// no engine with matching name found
		throw new IllegalArgumentException("passed engine " + namet
				+ " does not exist");
	}

	public static List<VirtualEngine> build(List<String> names) {
		List<VirtualEngine> engines = new LinkedList<>();
		for (String name : names) {
			engines.add(VirtualizedEngines.build(name));
		}
		return engines;
	}

}
