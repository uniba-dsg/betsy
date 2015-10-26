package betsy.bpmn.cli;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.cli.CliParameter;

import java.util.List;

public interface BPMNCliParameter extends CliParameter {
    List<AbstractBPMNEngine> getEngines();
    List<BPMNProcess> getProcesses();
}
