package betsy.repositories

import betsy.data.engines.Engine
import betsy.data.engines.activebpel.ActiveBpelEngine
import betsy.data.engines.bpelg.BpelgEngine
import betsy.data.engines.bpelg.BpelgInMemoryEngine
import betsy.data.engines.ode.Ode136Engine
import betsy.data.engines.ode.Ode136InMemoryEngine
import betsy.data.engines.ode.OdeEngine
import betsy.data.engines.ode.OdeInMemoryEngine
import betsy.data.engines.openesb.OpenEsb23Engine
import betsy.data.engines.openesb.OpenEsbEngine
import betsy.data.engines.orchestra.OrchestraEngine
import betsy.data.engines.petalsesb.PetalsEsb41Engine
import betsy.data.engines.petalsesb.PetalsEsbEngine
import betsy.virtual.host.engines.VirtualActiveBpelEngine
import betsy.virtual.host.engines.VirtualBpelgEngine
import betsy.virtual.host.engines.VirtualOdeEngine
import betsy.virtual.host.engines.VirtualOpenEsbEngine
import betsy.virtual.host.engines.VirtualOrchestraEngine
import betsy.virtual.host.engines.VirtualPetalsEsbEngine
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
                new Ode136Engine(),
                new OdeInMemoryEngine(),
                new Ode136InMemoryEngine(),
                new BpelgInMemoryEngine()
        ]
        List<Engine> vms = [
                new VirtualOdeEngine(),
                new VirtualBpelgEngine(),
                new VirtualOpenEsbEngine(),
                new VirtualPetalsEsbEngine(),
                new VirtualOrchestraEngine(),
                new VirtualActiveBpelEngine()
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
