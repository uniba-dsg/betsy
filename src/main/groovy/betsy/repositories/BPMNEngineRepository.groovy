package betsy.repositories

import betsy.data.engines.BPMNEngine
import betsy.data.engines.camunda.CamundaEngine
import betsy.data.engines.jbpm.JbpmEngine

class BPMNEngineRepository {

    private static final CamundaEngine CAMUNDA = new CamundaEngine()
    private static final JbpmEngine JBPM = new JbpmEngine()

    private Repository<BPMNEngine> repo = new Repository<>()

    public BPMNEngineRepository(){
        List<BPMNEngine> all = [
                CAMUNDA,
                JBPM
        ]
        repo.put("ALL", all)

        // insert every engine into the map
        for (BPMNEngine engine : repo.getByName("ALL")) {
            repo.put(engine.name, [engine])
        }
    }

    public List<BPMNEngine> getByName(String name) {
        return repo.getByName(name);
    }

    public List<BPMNEngine> getByNames(String[] names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }
}
