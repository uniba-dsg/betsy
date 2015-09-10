package betsy.bpmn;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestSuite;
import betsy.bpmn.validation.BPMNValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPMNBetsy {

    private String testFolderName;
    private List<AbstractBPMNEngine> engines = new ArrayList<>();
    private List<BPMNProcess> processes = new ArrayList<>();
    private BPMNComposite composite = new BPMNComposite();

    public void execute() {
        new BPMNValidator().validate();

        Collections.sort(processes);
        BPMNTestSuite testSuite = BPMNTestSuite.createTests(engines, processes, testFolderName);

        composite.setTestSuite(testSuite);
        composite.execute();
    }

    public void setTestFolder(String testFolderName) {
        this.testFolderName = testFolderName;
    }

    public List<AbstractBPMNEngine> getEngines() {
        return engines;
    }

    public void setEngines(List<AbstractBPMNEngine> engines) {
        this.engines = engines;
    }

    public List<BPMNProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(List<BPMNProcess> processes) {
        this.processes = processes;
    }

    public BPMNComposite getComposite() {
        return composite;
    }

    public void setComposite(BPMNComposite composite) {
        this.composite = composite;
    }
}
