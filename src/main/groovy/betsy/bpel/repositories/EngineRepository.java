package betsy.bpel.repositories;

import betsy.bpel.engines.Engine;
import betsy.bpel.engines.activebpel.ActiveBpelEngine;
import betsy.bpel.engines.bpelg.BpelgEngine;
import betsy.bpel.engines.bpelg.BpelgInMemoryEngine;
import betsy.bpel.engines.ode.Ode136Engine;
import betsy.bpel.engines.ode.Ode136InMemoryEngine;
import betsy.bpel.engines.ode.OdeEngine;
import betsy.bpel.engines.ode.OdeInMemoryEngine;
import betsy.bpel.engines.openesb.OpenEsb231Engine;
import betsy.bpel.engines.openesb.OpenEsb23Engine;
import betsy.bpel.engines.openesb.OpenEsbEngine;
import betsy.bpel.engines.orchestra.OrchestraEngine;
import betsy.bpel.engines.petalsesb.PetalsEsb41Engine;
import betsy.bpel.engines.petalsesb.PetalsEsbEngine;
import betsy.bpel.engines.wso2.Wso2Engine_v2_1_2;
import betsy.bpel.engines.wso2.Wso2Engine_v3_0_0;
import betsy.bpel.engines.wso2.Wso2Engine_v3_1_0;
import betsy.common.repositories.Repository;
import betsy.bpel.virtual.host.engines.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CAPITAL LETTERS for GROUPS of engines, lower case letters for engines
 */
public class EngineRepository {
    private static final BpelgEngine BPELG = new BpelgEngine();
    private static final OrchestraEngine ORCHESTRA = new OrchestraEngine();
    private static final ActiveBpelEngine ACTIVE_BPEL = new ActiveBpelEngine();
    private static final OpenEsb23Engine OPENESB_23 = new OpenEsb23Engine();
    private static final PetalsEsb41Engine PETALS_41 = new PetalsEsb41Engine();
    private static final Ode136Engine ODE_136 = new Ode136Engine();
    private static final OdeEngine ODE = new OdeEngine();
    private static final OdeInMemoryEngine ODE_IN_MEMORY = new OdeInMemoryEngine();
    private static final BpelgInMemoryEngine BPELG_IN_MEMORY = new BpelgInMemoryEngine();
    private static final Ode136InMemoryEngine ODE_136_IN_MEMORY = new Ode136InMemoryEngine();
    private static final OpenEsbEngine OPENESB = new OpenEsbEngine();
    private static final PetalsEsbEngine PETALS = new PetalsEsbEngine();
    private static final Wso2Engine_v3_1_0 WSO2_310 = new Wso2Engine_v3_1_0();
    private static final OpenEsb231Engine OPENESB231 = new OpenEsb231Engine();
    private Repository<Engine> repo = new Repository<>();

    public EngineRepository() {
        List<Engine> locals = Arrays.asList(ODE, BPELG, OPENESB, PETALS, ORCHESTRA, ACTIVE_BPEL, OPENESB_23, OPENESB231,
                PETALS_41, ODE_136, ODE_IN_MEMORY, ODE_136_IN_MEMORY, BPELG_IN_MEMORY, WSO2_310, new Wso2Engine_v3_0_0(), new Wso2Engine_v2_1_2());
        List<Engine> recent = Arrays.asList(BPELG, ORCHESTRA, ACTIVE_BPEL, OPENESB231, PETALS_41, ODE_136, WSO2_310);
        List<Engine> vms = Arrays.asList(new VirtualOdeEngine(), new VirtualBpelgEngine(), new VirtualOpenEsbEngine(),
                new VirtualPetalsEsbEngine(), new VirtualOrchestraEngine(), new VirtualActiveBpelEngine());
        List<Engine> all = new ArrayList<>();
        all.addAll(locals);
        all.addAll(vms);

        repo.put("ALL", all);
        repo.put("LOCALS", locals);
        repo.put("VMS", vms);
        repo.put("RECENT", recent);

        // insert every engine into the map
        for (Engine engine : repo.getByName("ALL")) {
            repo.put(engine.getName(), Arrays.asList(engine));
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
