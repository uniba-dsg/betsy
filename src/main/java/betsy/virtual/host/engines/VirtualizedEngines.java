package betsy.virtual.host.engines;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class VirtualizedEngines {

	/**
	 * Returns a list of all available virtualized engines.
	 * 
	 * @return a list of all available virtualized engines
	 */
	public static List<VirtualizedEngine> availableEngines() {
		LinkedList<VirtualizedEngine> engines = new LinkedList<>();
		engines.add(new VirtualizedOdeEngine());
		engines.add(new VirtualizedBpelgEngine());
		engines.add(new VirtualizedOpenEsbEngine());
		engines.add(new VirtualizedPetalsEsbEngine());
		engines.add(new VirtualizedOrchestraEngine());
		engines.add(new VirtualizedActiveBpelEngine());

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
		String trimedName = name.trim();

		for (VirtualizedEngine ve : availableEngines()) {
			if (ve.getName().equals(trimedName)) {
				return ve;
			}
		}

		// no engine with matching name found
		throw new IllegalArgumentException("passed engine " + trimedName
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
