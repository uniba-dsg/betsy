package betsy.bpel;

import betsy.bpel.engines.Engine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Betsy {
    private List<Engine> engines = new ArrayList<>();
    private List<BPELProcess> processes = new ArrayList<>();
    private Composite composite = new Composite();

    public void execute() {
        validate();

        Collections.sort(processes);

        BPELTestSuite testSuite = BPELTestSuite.createTests(engines, processes);

        composite.setTestSuite(testSuite);
        composite.execute();
    }

    private void validate() {
        new Validator(processes).validate();
    }

    public List<Engine> getEngines() {
        return engines;
    }

    public void setEngines(List<Engine> engines) {
        this.engines = engines;
    }

    public List<BPELProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(List<BPELProcess> processes) {
        this.processes = processes;
    }

    public Composite getComposite() {
        return composite;
    }

    public void setComposite(Composite composite) {
        this.composite = composite;
    }
}
