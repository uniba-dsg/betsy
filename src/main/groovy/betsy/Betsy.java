package betsy;

import betsy.data.BetsyProcess;
import betsy.data.TestSuite;
import betsy.data.engines.Engine;
import betsy.executables.Composite;
import betsy.executables.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Betsy {
    private List<Engine> engines = new ArrayList<>();
    private List<BetsyProcess> processes = new ArrayList<>();
    private Composite composite = new Composite();

    public void execute() throws Exception {
        validate();

        Collections.sort(processes);

        TestSuite testSuite = TestSuite.createTests(engines, processes);

        composite.setTestSuite(testSuite);
        composite.execute();
    }

    private void validate() {
        Validator validator = new Validator();
        validator.setProcesses(processes);
        validator.validate();
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
