package betsy.bpel;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestSuite;
import betsy.bpel.validation.BPELValidator;
import betsy.common.model.input.EngineIndependentProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BPELBetsy {
    private List<AbstractBPELEngine> engines = new ArrayList<>();
    private List<BPELProcess> processes = new ArrayList<>();
    private BPELComposite composite = new BPELComposite();
    private String testFolderName;

    public void execute() {
        Objects.requireNonNull(testFolderName, "test folder must be set");

        Collections.sort(processes);

        BPELTestSuite testSuite = BPELTestSuite.createTests(engines, processes, testFolderName);

        composite.setTestSuite(testSuite);
        composite.execute();
    }

    public List<AbstractBPELEngine> getEngines() {
        return engines;
    }

    public void setEngines(List<AbstractBPELEngine> engines) {
        this.engines = engines;
    }

    public List<BPELProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(List<EngineIndependentProcess> processes) {
        new BPELValidator(Objects.requireNonNull(processes)).validate();

        List<BPELProcess> processList = new ArrayList<>();
        for(EngineIndependentProcess engineIndependentProcess : processes) {
            processList.add(new BPELProcess(engineIndependentProcess));
        }
        this.processes = processList;
    }

    public void setTestFolder(String folderName){
        testFolderName = folderName;
    }

    public BPELComposite getComposite() {
        return composite;
    }

    public void setComposite(BPELComposite composite) {
        this.composite = composite;
    }
}
