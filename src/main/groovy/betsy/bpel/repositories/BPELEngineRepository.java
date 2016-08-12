package betsy.bpel.repositories;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.engines.activebpel.ActiveBpelEngine;
import betsy.bpel.engines.bpelg.BpelgEngine;
import betsy.bpel.engines.bpelg.BpelgInMemoryEngine;
import betsy.bpel.engines.ode.Ode136Engine;
import betsy.bpel.engines.ode.Ode136InMemoryEngine;
import betsy.bpel.engines.ode.OdeEngine;
import betsy.bpel.engines.ode.OdeInMemoryEngine;
import betsy.bpel.engines.openesb.*;
import betsy.bpel.engines.orchestra.OrchestraEngine;
import betsy.bpel.engines.petalsesb.PetalsEsb41Engine;
import betsy.bpel.engines.petalsesb.PetalsEsbEngine;
import betsy.bpel.engines.wso2.Wso2Engine_v2_1_2;
import betsy.bpel.engines.wso2.Wso2Engine_v3_0_0;
import betsy.bpel.engines.wso2.Wso2Engine_v3_1_0;
import betsy.bpel.engines.wso2.Wso2Engine_v3_2_0;
import betsy.bpel.engines.wso2.Wso2Engine_v3_5_1;
import betsy.bpel.virtual.host.engines.*;
import betsy.common.repositories.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CAPITAL LETTERS for GROUPS of engines, lower case letters for engines
 */
public class BPELEngineRepository {

    private final Repository<AbstractBPELEngine> repo = new Repository<>();

    public BPELEngineRepository() {
        List<AbstractBPELEngine> locals = Arrays.asList(
                new OdeEngine(), new Ode136Engine(), new OdeInMemoryEngine(), new Ode136InMemoryEngine(),
                new OpenEsbEngine(),
                new OpenEsb23Engine(),
                new OpenEsb231Engine(),
                new OpenEsb301StandaloneEngine(), new OpenEsb305StandaloneEngine(),
                new OrchestraEngine(),
                new ActiveBpelEngine(),
                new PetalsEsbEngine(), new PetalsEsb41Engine(),
                new BpelgEngine(), new BpelgInMemoryEngine(),
                new Wso2Engine_v3_5_1(), new Wso2Engine_v3_2_0(), new Wso2Engine_v3_1_0(), new Wso2Engine_v3_0_0(), new Wso2Engine_v2_1_2());
        List<AbstractBPELEngine> recent = Arrays.asList(
                new BpelgEngine(),
                new OrchestraEngine(),
                new ActiveBpelEngine(),
                new OpenEsb305StandaloneEngine(),
                new PetalsEsb41Engine(),
                new Ode136Engine(),
                new Wso2Engine_v3_2_0());
        List<AbstractBPELEngine> vms = Arrays.asList(
                new VirtualOdeEngine(),
                new VirtualBpelgEngine(),
                new VirtualOpenEsbEngine(),
                new VirtualPetalsEsbEngine(),
                new VirtualOrchestraEngine(),
                new VirtualActiveBpelEngine());
        List<AbstractBPELEngine> all = new ArrayList<>();
        all.addAll(locals);
        all.addAll(vms);

        repo.put("ALL", all);
        repo.put("LOCALS", locals);
        repo.put("VMS", vms);
        repo.put("RECENT", recent);

        // insert every engine into the map
        for (AbstractBPELEngine engine : repo.getByName("ALL")) {
            repo.put(engine.getName(), Collections.singletonList(engine));
        }

        repo.put("ode", repo.getByName("ode__1_3_6"));
        repo.put("bpelg", repo.getByName("bpelg__5_3"));
        repo.put("wso2", repo.getByName("wso2__3_5_1"));
        repo.put("petalsesb", repo.getByName("petalsesb__4_1"));
        repo.put("active_bpel", repo.getByName("activebpel__5_0_2"));
        repo.put("orchestra", repo.getByName("orchestra__4_9"));
        repo.put("openesb", repo.getByName("openesb__3_0_5"));
    }

    public List<AbstractBPELEngine> getByName(String name) {
        return repo.getByName(name);
    }

    public List<AbstractBPELEngine> getByNames(String... names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }
}
