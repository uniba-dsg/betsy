package betsy.bpmn.repositories;

import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.engines.activiti.ActivitiEngine;
import betsy.bpmn.engines.camunda.Camunda710Engine;
import betsy.bpmn.engines.camunda.CamundaEngine;
import betsy.bpmn.engines.jbpm.JbpmEngine;
import betsy.bpmn.engines.jbpm.JbpmEngine610;
import betsy.common.repositories.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BPMNEngineRepository {
    public BPMNEngineRepository() {
        List<BPMNEngine> all = new ArrayList<>(Arrays.asList(new CamundaEngine(), new Camunda710Engine(), new ActivitiEngine(), new JbpmEngine(), new JbpmEngine610()));
        repo.put("ALL", all);

        // insert every engine into the map
        for (BPMNEngine engine : repo.getByName("ALL")) {
            repo.put(engine.getName(), new ArrayList<>(Arrays.asList(engine)));
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

    private Repository<BPMNEngine> repo = new Repository<>();
}
