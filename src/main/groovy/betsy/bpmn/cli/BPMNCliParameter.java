package betsy.bpmn.cli;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;

import java.util.List;

public interface BPMNCliParameter {

    List<AbstractBPMNEngine> getEngines();
    List<BPMNProcess> getProcesses();
    String getTestFolderName();

    boolean openResultsInBrowser();
    boolean buildArtifactsOnly();
    boolean showHelp();

}
