package peal.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.common.engines.EngineAPI;
import peal.InstanceService;
import peal.InstanceStateDetailed;
import peal.Variable;
import peal.helper.ZipFileHelper;
import peal.identifier.InstanceId;
import peal.identifier.ProcessModelId;
import peal.observer.InstanceState;
import peal.packages.LogPackage;

public class InstanceServiceImpl implements InstanceService {

    private final EngineServiceImpl engineService;

    public InstanceServiceImpl(EngineServiceImpl engineService) {
        this.engineService = Objects.requireNonNull(engineService);
    }

    @Override
    public InstanceId start(ProcessModelId processID, List<Variable> variables) throws RuntimeException {
        EngineAPI<?> engine = engineService.getEngineByID(processID.toEngineId());
        AbstractBPMNEngine bpmnEngine = (AbstractBPMNEngine) engine;
        BPMNProcessStarter processStarter = bpmnEngine.getProcessStarter();
        List<pebl.test.steps.Variable> variableList = new LinkedList<>();
        for (Variable variable : variables) {
            variableList.add(new pebl.test.steps.Variable(variable.getName(), variable.getType(), variable.getValue()));
        }
        processStarter.start(processID.getProcessId().getLocalPart(), variableList);

        return new InstanceId(processID.getEngineId(), processID.getProcessId(), "1");
    }

    @Override
    public InstanceStateDetailed getStateDetailed(InstanceId instanceId) {
        /*
        BPMNProcessInstanceOutcomeChecker processInstanceOutcomeChecker;
        BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome processInstanceOutcome = processInstanceOutcomeChecker.checkProcessOutcome(instanceId.getProcessId().getLocalPart());
        if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
            return InstanceStateDetailed.UNDEPLOYED_PROCESS;
        } else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNKNOWN){
            return InstanceStateDetailed.UNKNOWN;
        } else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.OK){
            return InstanceStateDetailed.OK;
        }else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.COULD_NOT_CHECK_PROCESS_INSTANCE_STATUS){
            return InstanceStateDetailed.COULD_NOT_CHECK_PROCESS_INSTANCE_STATUS;
        }else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED){
            return InstanceStateDetailed.PROCESS_INSTANCE_ABORTED;
        }else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN){
            return InstanceStateDetailed.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
        }else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN){
            return InstanceStateDetailed.PROCESS_INSTANCE_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN;
        }else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.RUNTIME){
            return InstanceStateDetailed.RUNTIME;
        } else {
            throw new IllegalStateException("outcome " + processInstanceOutcome + "not mappable");
        }
        */
        return null;
    }

    @Override
    public InstanceState getState(InstanceId instanceId) {
        Path logForInstance = getInstanceLog(instanceId);

        if (!Files.exists(logForInstance)) {
            return InstanceState.NOT_STARTED;
        } else {
            return InstanceState.STOPPED;
        }

        /*
        BPMNProcessInstanceOutcomeChecker processInstanceOutcomeChecker;
        BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome processInstanceOutcome = processInstanceOutcomeChecker.checkProcessOutcome(instanceId.getProcessId().getLocalPart());
        if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
            return InstanceState.NOT_STARTED;
        } else if(processInstanceOutcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNKNOWN){
            return InstanceState.STARTED;
        } else {
            return InstanceState.STOPPED;
        }
        */
    }

    @Override
    public LogPackage getLogs(InstanceId instanceId) {
        Path logForInstance = getInstanceLog(instanceId);
        List<Path> logs = Collections.singletonList(logForInstance);
        return ZipFileHelper.createLogPackage(logs);
    }

    private Path getInstanceLog(InstanceId instanceId) {
        EngineAPI<?> engine = engineService.getEngineByID(instanceId.toEngineId());
        AbstractBPMNEngine bpmnEngine = (AbstractBPMNEngine) engine;
        return bpmnEngine.getLogForInstance(instanceId.getInstanceID());
    }

}
