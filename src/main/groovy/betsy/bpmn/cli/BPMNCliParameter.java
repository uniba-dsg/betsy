package betsy.bpmn.cli;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.cli.CliParameter;
import pebl.test.Test;

import java.util.List;

public interface BPMNCliParameter extends CliParameter {
    List<AbstractBPMNEngine> getEngines();
    List<Test> getProcesses();
}
