package betsy.data

import betsy.data.engines.*

class Engines {

    /**
     * Returns a list of all available engines.
     *
     * @return a list of all available engines
     */
    public static List<Engine> availableEngines() {
        [new OdeEngine(), new BpelgEngine(), new OpenEsbEngine(), new PetalsEsbEngine(),
                new OrchestraEngine(), new ActiveBpelEngine(), new PetalsEsb41Engine(),
                new OpenEsb23Engine(),
                new Ode136Engine()
        ]
    }

    /**
     * Find an engine by name.
     *
     * @param name the name of an engine
     * @return the engine if it can be found
     * @throws IllegalArgumentException if the engine can not be found
     */
    public static Engine build(String name) {
        Engine engine = availableEngines().find { it.name == name }

        if (engine == null) {
            throw new IllegalArgumentException("Engine $name does not exist")
        } else {
            return engine
        }
    }

    public static List<Engine> build(List<String> names) {
        names.collect { build(it) }
    }

}
