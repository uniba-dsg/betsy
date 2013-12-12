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
import betsy.data.engines.wso2.Wso2Engine
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

    private static final BpelgEngine BPELG = new BpelgEngine()
    private static final OrchestraEngine ORCHESTRA = new OrchestraEngine()
    private static final ActiveBpelEngine ACTIVE_BPEL = new ActiveBpelEngine()
    private static final OpenEsb23Engine OPENESB_23 = new OpenEsb23Engine()
    private static final PetalsEsb41Engine PETALS_41 = new PetalsEsb41Engine()
    private static final Ode136Engine ODE_136 = new Ode136Engine()

    private Repository<Engine> repo = new Repository<>();

    public EngineRepository() {
        List<Engine> locals = [
                new OdeEngine(),
                BPELG,
                new OpenEsbEngine(),
                new PetalsEsbEngine(),
                ORCHESTRA,
                ACTIVE_BPEL,
                OPENESB_23,
                PETALS_41,
                ODE_136,
                new OdeInMemoryEngine(),
                new Ode136InMemoryEngine(),
                new BpelgInMemoryEngine(),
                new Wso2Engine()
        ]

        List<Engine> recent = [
                BPELG,
                ORCHESTRA,
                ACTIVE_BPEL,
                OPENESB_23,
                PETALS_41,
                ODE_136,
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
        repo.put("RECENT", recent)

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
