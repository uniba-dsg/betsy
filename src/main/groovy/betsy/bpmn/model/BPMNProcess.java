package betsy.bpmn.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.model.ProcessFolderStructure;
import betsy.common.model.engine.EngineDimension;
import betsy.common.model.engine.EngineExtended;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureDimension;
import pebl.benchmark.feature.Group;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.TestCase;

public class BPMNProcess implements ProcessFolderStructure, Comparable<BPMNProcess>, FeatureDimension, EngineDimension {

    private Test test;
    private AbstractBPMNEngine engine;
    private Path deploymentPackagePath;

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Path getDeploymentPackagePath() {
        return deploymentPackagePath;
    }

    public void setDeploymentPackagePath(Path deploymentPackagePath) {
        this.deploymentPackagePath = deploymentPackagePath;
    }

    public BPMNProcess(Test test) {
        this.test = Objects.requireNonNull(test);
    }

    public BPMNProcess createCopyWithoutEngine() {
        return new BPMNProcess(test);
    }

    public void setEngine(AbstractBPMNEngine engine) {
        this.engine = engine;
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
        return test.getProcess();
    }

    @Override
    public Group getGroup() {
        return this.test.getGroup();
    }

    @Override
    public Feature getFeature() {
        return test.getFeature();
    }

    public List<TestCase> getTestCases() {
        return test.getTestCases().stream().map(p -> (TestCase) p).collect(Collectors.toList());
    }

    @Override
    public int compareTo(BPMNProcess o) {
        return test.compareTo(o.test);
    }

    public String getDescription() {
        return test.getDescription();
    }

    @Override
    public EngineExtended getEngineObject() {
        return getEngine().getEngineObject();
    }

    public FeatureDimension getFeatureDimension() {
        return test;
    }

    @Override
    public String getGroupName() {
        return getFeatureDimension().getGroup().getName();
    }

    public String getPackageID() {
        return String.join(".", getEngineID(), getGroup().getName());
    }
}
