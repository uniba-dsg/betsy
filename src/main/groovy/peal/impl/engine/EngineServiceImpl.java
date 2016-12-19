package peal.impl.engine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jws.WebMethod;
import javax.jws.WebService;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.engines.activebpel.ActiveBpelEngine;
import betsy.bpel.engines.bpelg.BpelgEngine;
import betsy.bpel.engines.bpelg.BpelgInMemoryEngine;
import betsy.bpel.engines.ode.Ode136Engine;
import betsy.bpel.engines.ode.Ode136InMemoryEngine;
import betsy.bpel.engines.ode.OdeEngine;
import betsy.bpel.engines.ode.OdeInMemoryEngine;
import betsy.bpel.engines.openesb.OpenEsb231Engine;
import betsy.bpel.engines.openesb.OpenEsb23Engine;
import betsy.bpel.engines.openesb.OpenEsb301StandaloneEngine;
import betsy.bpel.engines.openesb.OpenEsb305StandaloneEngine;
import betsy.bpel.engines.openesb.OpenEsbEngine;
import betsy.bpel.engines.orchestra.OrchestraEngine;
import betsy.bpel.engines.petalsesb.PetalsEsb41Engine;
import betsy.bpel.engines.petalsesb.PetalsEsbEngine;
import betsy.bpel.engines.wso2.Wso2Engine_v2_1_2;
import betsy.bpel.engines.wso2.Wso2Engine_v3_0_0;
import betsy.bpel.engines.wso2.Wso2Engine_v3_1_0;
import betsy.bpel.engines.wso2.Wso2Engine_v3_2_0;
import betsy.bpel.engines.wso2.Wso2Engine_v3_5_1;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.activiti.Activiti5151Engine;
import betsy.bpmn.engines.activiti.Activiti5170Engine;
import betsy.bpmn.engines.activiti.Activiti5180Engine;
import betsy.bpmn.engines.activiti.Activiti51902Engine;
import betsy.bpmn.engines.activiti.Activiti5190Engine;
import betsy.bpmn.engines.activiti.Activiti5200Engine;
import betsy.bpmn.engines.activiti.Activiti5210Engine;
import betsy.bpmn.engines.activiti.ActivitiEngine;
import betsy.bpmn.engines.camunda.Camunda710Engine;
import betsy.bpmn.engines.camunda.Camunda720Engine;
import betsy.bpmn.engines.camunda.Camunda730Engine;
import betsy.bpmn.engines.camunda.Camunda740Engine;
import betsy.bpmn.engines.camunda.Camunda750Engine;
import betsy.bpmn.engines.camunda.Camunda760Engine;
import betsy.bpmn.engines.camunda.CamundaEngine;
import betsy.bpmn.engines.jbpm.JbpmEngine;
import betsy.bpmn.engines.jbpm.JbpmEngine610;
import betsy.bpmn.engines.jbpm.JbpmEngine620;
import betsy.bpmn.engines.jbpm.JbpmEngine630;
import betsy.bpmn.engines.jbpm.JbpmEngine640;
import betsy.bpmn.engines.jbpm.JbpmEngine650;
import betsy.common.engines.EngineAPI;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.engine.EngineDimension;
import peal.EngineService;
import peal.ProcessLanguage;
import peal.helper.ZipFileHelper;
import peal.identifier.EngineId;
import peal.observer.EngineState;
import peal.packages.LogPackage;

@WebService
public class EngineServiceImpl implements EngineService {

    private final List<EngineAPI<?>> engineIds = Arrays.asList(
            new OdeEngine(),
            new Ode136Engine(),
            new OdeInMemoryEngine(),
            new Ode136InMemoryEngine(),

            new OpenEsbEngine(),
            new OpenEsb23Engine(),
            new OpenEsb231Engine(),
            new OpenEsb301StandaloneEngine(),
            new OpenEsb305StandaloneEngine(),

            new OrchestraEngine(),

            new ActiveBpelEngine(),

            new PetalsEsbEngine(),
            new PetalsEsb41Engine(),

            new BpelgEngine(),
            new BpelgInMemoryEngine(),

            new Wso2Engine_v3_5_1(),
            new Wso2Engine_v3_2_0(),
            new Wso2Engine_v3_1_0(),
            new Wso2Engine_v3_0_0(),
            new Wso2Engine_v2_1_2(),

            new CamundaEngine(),
            new Camunda710Engine(),
            new Camunda720Engine(),
            new Camunda730Engine(),
            new Camunda740Engine(),
            new Camunda750Engine(),
            new Camunda760Engine(),

            new Activiti5151Engine(),
            new ActivitiEngine(),
            new Activiti5170Engine(),
            new Activiti5180Engine(),
            new Activiti5190Engine(),
            new Activiti51902Engine(),
            new Activiti5200Engine(),
            new Activiti5210Engine(),

            new JbpmEngine(),
            new JbpmEngine610(),
            new JbpmEngine620(),
            new JbpmEngine630(),
            new JbpmEngine640(),
            new JbpmEngine650()

    );
    private List<EngineId> engines = engineIds.stream()
            .map(EngineDimension::getEngineObject)
            .map(Object::toString)
            .map(EngineId::new)
            .collect(Collectors.toList());

    public EngineServiceImpl() {
        // required so that each engine has its correct folder
        for (EngineAPI<?> engine : engineIds) {
            Path parentFolder = Paths.get("test-" + engine.getName());
            if (engine instanceof AbstractLocalBPELEngine) {
                ((AbstractLocalBPELEngine) engine).setParentFolder(parentFolder);
            } else if (engine instanceof AbstractBPMNEngine) {
                ((AbstractBPMNEngine) engine).setParentFolder(parentFolder);
            }
        }
    }

    @Override
    public Set<EngineId> getSupportedEngines() {
        //based on id of engine
        return new HashSet<>(engines);
    }

    @Override
    public void install(EngineId engineId) {
        getEngineByID(engineId).install();
    }

    @Override
    public void uninstall(EngineId engineId) {
        getEngineByID(engineId).uninstall();
    }

    @WebMethod(exclude = true)
    public EngineAPI<?> getEngineByID(EngineId engineId) {
        int index = engines.indexOf(engineId);
        if (index == -1) {
            throw new IllegalArgumentException("EngineExtended id " + engineId + " not found");
        }
        return engineIds.get(index);
    }

    @Override
    public ProcessLanguage getSupportedLanguage(EngineId engineId) {
        pebl.ProcessLanguage language = getEngineByID(engineId).getEngineObject().getLanguage();
        return convertProcessLanguage(language);
    }

    private static ProcessLanguage convertProcessLanguage(pebl.ProcessLanguage language) {
        if (language == pebl.ProcessLanguage.BPEL) {
            return ProcessLanguage.BPEL;
        } else {
            return ProcessLanguage.BPMN;
        }
    }

    @Override
    public void start(EngineId engineId) {
        getEngineByID(engineId).startup();
    }

    @Override
    public void stop(EngineId engineId) {
        try {
            getEngineByID(engineId).shutdown();
        } catch (IllegalArgumentException ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public EngineState getState(EngineId engineId) {
        EngineLifecycle engineLifecycle = getEngineByID(engineId);
        return convertEngineLifecycleToEngineState(engineLifecycle);
    }

    private static EngineState convertEngineLifecycleToEngineState(EngineLifecycle engineLifecycle) {
        if (engineLifecycle.isRunning()) {
            return EngineState.STARTED;
        } else if (engineLifecycle.isInstalled()) {
            return EngineState.INSTALLED;
        } else {
            return EngineState.NOT_INSTALLED;
        }
    }

    @Override
    public LogPackage getLogs(EngineId engineId) {
        EngineAPI<?> abstractLocalBPELEngine = getEngineByID(engineId);
        return ZipFileHelper.createLogPackage(abstractLocalBPELEngine.getLogs());
    }

}
