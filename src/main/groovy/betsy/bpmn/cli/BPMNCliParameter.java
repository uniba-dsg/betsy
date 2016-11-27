package betsy.bpmn.cli;

import java.util.List;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.cli.CliParameter;
import pebl.benchmark.test.Test;

public interface BPMNCliParameter extends CliParameter {
    List<AbstractBPMNEngine> getEngines();
    List<Test> getProcesses();
}
