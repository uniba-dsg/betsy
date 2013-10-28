package betsy.data.engines

import betsy.data.engines.activeBpel.ActiveBpelEngine
import betsy.data.engines.bpelg.BpelgEngine
import betsy.data.engines.ode.Ode136Engine
import betsy.data.engines.ode.OdeEngine
import betsy.data.engines.openEsb.OpenEsb23Engine
import betsy.data.engines.openEsb.OpenEsbEngine
import betsy.data.engines.orchestra.OrchestraEngine
import betsy.data.engines.petalsEsb.PetalsEsb41Engine
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
                new ActiveBpelEngine(),
                new OpenEsb23Engine(),
                new PetalsEsb41Engine(),
                new Ode136Engine()
        ]
    }

}
