package betsy.bpmn.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.activiti.Activiti5151Engine;
import betsy.bpmn.engines.activiti.Activiti5170Engine;
import betsy.bpmn.engines.activiti.Activiti5180Engine;
import betsy.bpmn.engines.activiti.Activiti51902Engine;
import betsy.bpmn.engines.activiti.Activiti5190Engine;
import betsy.bpmn.engines.activiti.Activiti5200Engine;
import betsy.bpmn.engines.activiti.Activiti5210Engine;
import betsy.bpmn.engines.activiti.Activiti5220Engine;
import betsy.bpmn.engines.activiti.ActivitiEngine;
import betsy.bpmn.engines.camunda.Camunda710Engine;
import betsy.bpmn.engines.camunda.Camunda720Engine;
import betsy.bpmn.engines.camunda.Camunda730Engine;
import betsy.bpmn.engines.camunda.Camunda740Engine;
import betsy.bpmn.engines.camunda.Camunda750Engine;
import betsy.bpmn.engines.camunda.Camunda760Engine;
import betsy.bpmn.engines.camunda.CamundaEngine;
import betsy.bpmn.engines.flowable.Flowable5220Engine;
import betsy.bpmn.engines.jbpm.JbpmEngine;
import betsy.bpmn.engines.jbpm.JbpmEngine610;
import betsy.bpmn.engines.jbpm.JbpmEngine620;
import betsy.bpmn.engines.jbpm.JbpmEngine630;
import betsy.bpmn.engines.jbpm.JbpmEngine640;
import betsy.bpmn.engines.jbpm.JbpmEngine650;
import betsy.common.repositories.Repository;

public class BPMNEngineRepository {
    private final Repository<AbstractBPMNEngine> repo = new Repository<>();

    public BPMNEngineRepository() {
        List<AbstractBPMNEngine> all = Arrays.asList(
                new CamundaEngine(), new Camunda710Engine(), new Camunda720Engine(), new Camunda730Engine(), new Camunda740Engine(), new Camunda750Engine(), new Camunda760Engine(),
                new Activiti5151Engine(), new ActivitiEngine(), new Activiti5170Engine(), new Activiti5180Engine(), new Activiti51902Engine(), new Activiti5200Engine(), new Activiti5210Engine(), new Activiti5220Engine(),
                new Flowable5220Engine(),
                new JbpmEngine(), new JbpmEngine610(), new JbpmEngine620(), new JbpmEngine630(), new JbpmEngine640(), new JbpmEngine650());
        repo.put("ALL", all);

        // insert every engine into the map
        for (AbstractBPMNEngine engine : repo.getByName("ALL")) {
            repo.put(engine.getName(), Collections.singletonList(engine));
        }

        repo.put("activiti", repo.getByName("activiti__5_22_0"));
        repo.put("jbpm", repo.getByName("jbpm__6_5_0"));
        repo.put("flowable", repo.getByName("flowable__5_22_0"));
        repo.put("camunda", repo.getByName("camunda__7_6_0"));
    }

    public List<AbstractBPMNEngine> getByName(String name) {
        return repo.getByName(name);
    }

    public List<AbstractBPMNEngine> getByNames(String... names) {
        return repo.getByNames(names);
    }

    public List<String> getNames() {
        return repo.getNames();
    }

}
