package betsy.bpmn;

import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPMNBetsy {
    public void execute() throws Exception {
        Collections.sort(processes);
        BPMNTestSuite testSuite = BPMNTestSuite.createTests(engines, processes);

        composite.setTestSuite(testSuite);
        composite.execute();
    }

    public List<BPMNEngine> getEngines() {
        return engines;
    }

    public void setEngines(List<BPMNEngine> engines) {
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

    private List<BPMNEngine> engines = new ArrayList<>();
    private List<BPMNProcess> processes = new ArrayList<>();
    private BPMNComposite composite = new BPMNComposite();
}
