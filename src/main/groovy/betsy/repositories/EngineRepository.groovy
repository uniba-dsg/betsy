package betsy.repositories

import betsy.data.engines.Engine
import betsy.data.engines.activebpel.ActiveBpelEngine
import betsy.data.engines.bpelg.BpelgEngine
import betsy.data.engines.ode.Ode136Engine
import betsy.data.engines.ode.OdeEngine
import betsy.data.engines.openesb.OpenEsb23Engine
import betsy.data.engines.openesb.OpenEsbEngine
import betsy.data.engines.orchestra.OrchestraEngine
import betsy.data.engines.petalsesb.PetalsEsb41Engine
import betsy.data.engines.petalsesb.PetalsEsbEngine
import betsy.virtual.host.engines.VirtualizedActiveBpelEngine
import betsy.virtual.host.engines.VirtualizedBpelgEngine
import betsy.virtual.host.engines.VirtualizedOdeEngine
import betsy.virtual.host.engines.VirtualizedOpenEsbEngine
import betsy.virtual.host.engines.VirtualizedOrchestraEngine
import betsy.virtual.host.engines.VirtualizedPetalsEsbEngine
import betsy.repositories.Repository


/**
 * CAPITAL LETTERS for GROUPS of engines, lower case letters for engines
 */
class EngineRepository {

    private Repository<Engine> repo = new Repository<>();

    public EngineRepository() {
        List<Engine> locals = [
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
        List<Engine> vms = [
                new VirtualizedOdeEngine(),
                new VirtualizedBpelgEngine(),
                new VirtualizedOpenEsbEngine(),
                new VirtualizedPetalsEsbEngine(),
                new VirtualizedOrchestraEngine(),
                new VirtualizedActiveBpelEngine()
        ]
        List<Engine> all = [locals, vms].flatten()

        repo.put("ALL", all)
        repo.put("LOCALS", locals)
        repo.put("VMS", vms)

        // insert every engine into the map
        for (Engine engine : repo.getByName("ALL")) {
            repo.put(engine.name, [engine])
        }
    }

    public List<Engine> getByName(String name) {
        return repo.getByName(name);
    }

    public List<Engine> getByNames(String[] names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }

}
