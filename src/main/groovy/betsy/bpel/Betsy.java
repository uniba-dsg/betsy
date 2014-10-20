package betsy.bpel;

import betsy.bpel.engines.Engine;
import betsy.bpel.model.BetsyProcess;
import betsy.common.model.TestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Betsy {
    private List<Engine> engines = new ArrayList<>();
    private List<BetsyProcess> processes = new ArrayList<>();
    private Composite composite = new Composite();

    public void execute() {
        validate();

        Collections.sort(processes);

        TestSuite testSuite = TestSuite.createTests(engines, processes);

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

    public List<BetsyProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(List<BetsyProcess> processes) {
        this.processes = processes;
    }

    public Composite getComposite() {
        return composite;
    }

    public void setComposite(Composite composite) {
        this.composite = composite;
    }
}
