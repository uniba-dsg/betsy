package de.uniba.wiai.dsg.betsy.virtual.host;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualizedActiveBpelEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualizedBpelgEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualizedOdeEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualizedOpenEsbEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualizedOrchestraEngine;
import de.uniba.wiai.dsg.betsy.virtual.host.engines.VirtualizedPetalsEsbEngine;

public class VirtualizedEngines {

	/**
	 * Returns a list of all available virtualized engines.
	 * 
	 * @return a list of all available virtualized engines
	 */
	public static List<VirtualizedEngine> availableEngines() {
		VirtualBoxController vbc = new VirtualBoxController();
		vbc.init();

		LinkedList<VirtualizedEngine> engines = new LinkedList<>();
		engines.add(new VirtualizedOdeEngine(vbc));
		engines.add(new VirtualizedBpelgEngine(vbc));
		engines.add(new VirtualizedOpenEsbEngine(vbc));
		engines.add(new VirtualizedPetalsEsbEngine(vbc));
		engines.add(new VirtualizedOrchestraEngine(vbc));
		engines.add(new VirtualizedActiveBpelEngine(vbc));

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
	public static VirtualizedEngine build(final String name) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("name must not be null or empty");
		}
		String namet = name.trim();

		for (VirtualizedEngine ve : availableEngines()) {
			if (ve.getName().equals(namet)) {
				return ve;
			}
		}

		// no engine with matching name found
		throw new IllegalArgumentException("passed engine " + namet
				+ " does not exist");
	}

	/**
	 * Find all virtualized engines by a list of names.
	 * 
	 * @param names
	 *            the names of the virtualized engines
	 * @return the virtualized engines if they could be found
	 * @throws IllegalArgumentException
	 *             if any virtualized engine could not be found
	 */
	public static List<VirtualizedEngine> build(List<String> names) {
		List<VirtualizedEngine> engines = new LinkedList<>();
		for (String name : names) {
			engines.add(VirtualizedEngines.build(name));
		}
		return engines;
	}

}
