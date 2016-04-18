package betsy.bpmn.cli;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.cli.CliParameter;
import betsy.common.model.input.EngineIndependentProcess;

import java.util.List;

public interface BPMNCliParameter extends CliParameter {
    List<AbstractBPMNEngine> getEngines();
    List<EngineIndependentProcess> getProcesses();
}
