package betsy.bpmn.model;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNTester;
import betsy.common.HasPath;
import betsy.common.model.AbstractProcess;
import betsy.common.model.EngineIndependentProcess;
import betsy.common.model.ProcessFolderStructure;
import betsy.common.model.feature.Group;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BPMNProcess implements ProcessFolderStructure, Comparable<BPMNProcess> {

    private EngineIndependentProcess engineIndependentProcess;
    private AbstractBPMNEngine engine;

    public BPMNProcess(EngineIndependentProcess engineIndependentProcess) {
        this.engineIndependentProcess = Objects.requireNonNull(engineIndependentProcess);
    }

    public BPMNProcess createCopyWithoutEngine() {
        return new BPMNProcess(engineIndependentProcess);
    }

    public void setEngine(AbstractBPMNEngine engine) {
        this.engine = engine;
    }

    public String getGroupId() {
        return "de.uniba.dsg";
    }

    public String getVersion() {
        return "1.0";
    }

    public Path getTargetReportsPathWithCase(int testCaseNumber) {
        return getTargetReportsPath().resolve("case" + testCaseNumber);
    }

    public Path getTargetTestBinPath() {
        return getTargetPath().resolve("testBin");
    }

    public Path getTargetTestBinPathWithCase(int testCaseName) {
        return getTargetTestBinPath().resolve("case" + testCaseName);
    }

    public Path getTargetTestSrcPath() {
        return getTargetPath().resolve("testSrc");
    }

    public Path getTargetTestSrcPathWithCase(int testCaseNumber) {
        return getTargetTestSrcPath().resolve("case" + testCaseNumber);
    }

    @Override
    public AbstractBPMNEngine getEngine() {
        return engine;
    }

    @Override
    public Path getProcess() {
        return engineIndependentProcess.getProcess();
    }

    @Override
    public String getGroup() {
        return engineIndependentProcess.getGroup().getName();
    }

    @Override
    public Group getGroupObject() {
        return engineIndependentProcess.getGroup();
    }

    public List<BPMNTestCase> getTestCases() {
        return engineIndependentProcess.getTestCases().stream().map(p -> (BPMNTestCase) p).collect(Collectors.toList());
    }

    @Override
    public int compareTo(BPMNProcess o) {
        return engineIndependentProcess.compareTo(o.engineIndependentProcess);
    }
}
