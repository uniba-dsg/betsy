package betsy.data.engines

import java.util.List;

import betsy.data.engines.activeBpel.ActiveBpelEngine
import betsy.data.engines.bpelg.BpelgEngine;
import betsy.data.engines.ode.OdeEngine
import betsy.data.engines.openEsb.OpenEsbEngine
import betsy.data.engines.orchestra.OrchestraEngine
import betsy.data.engines.petalsEsb.PetalsEsbEngine

class LocalEngines {

	/**
	 * Returns a list of all available local engines.
	 *
	 * @return a list of all available local engines
	 */
	public static List<Engine> availableEngines() {
		[
			new OdeEngine(),
			new BpelgEngine(),
			new OpenEsbEngine(),
			new PetalsEsbEngine(),
			new OrchestraEngine(),
			new ActiveBpelEngine()
		]
	}

	/**
	 * Find a local engine by name.
	 *
	 * @param name the name of a local engine
	 * @return the local engine if it can be found
	 * @throws IllegalArgumentException if the local engine can not be found
	 */
	public static Engine build(String name) {
		LocalEngine engine = availableEngines().find {it.name == name}

		if (engine == null) {
			throw new IllegalArgumentException("LocalEngine $name does not exist")
		}

        return engine
	}

	public static List<Engine> build(List<String> names) {
		names.collect { Engine.build(it) }
	}
}
