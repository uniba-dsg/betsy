package betsy.repositories

import betsy.data.engines.BPMNEngine
import betsy.data.engines.camunda.CamundaEngine

class BPMNEngineRepository {

    private static final CamundaEngine CAMUNDA = new CamundaEngine()

    private Repository<BPMNEngine> repo = new Repository<>()

    public BPMNEngineRepository(){
        repo.put("ALL",
                CAMUNDA as List<BPMNEngine>)

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
