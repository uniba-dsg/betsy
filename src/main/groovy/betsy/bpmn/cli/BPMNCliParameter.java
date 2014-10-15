package betsy.bpmn.cli;

import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.model.BPMNProcess;

import java.util.List;

public interface BPMNCliParameter {

    List<BPMNEngine> getEngines();

    List<BPMNProcess> getProcesses();

    boolean openResultsInBrowser();
    boolean buildArtifactsOnly();
    boolean showHelp();

}
