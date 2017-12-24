package betsy.bpel.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.common.model.ProcessFolderStructure;
import betsy.common.model.engine.EngineDimension;
import betsy.common.model.engine.EngineExtended;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureDimension;
import pebl.benchmark.feature.Group;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.TestCase;

public class BPELProcess implements ProcessFolderStructure, Comparable<BPELProcess>, FeatureDimension, EngineDimension {

    private Test test;
    private AbstractBPELEngine engine;
    private Path deploymentPackagePath;

    public BPELProcess(Test test) {
        this.test = Objects.requireNonNull(test);
    }

    public BPELProcess createCopyWithoutEngine() {
        return new BPELProcess(test);
    }

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

    public String getEndpoint() {
        return getEngine().getEndpointUrl(this.getName());
    }

    public String getWsdlEndpoint() {
        return getEndpoint() + "?wsdl";
    }

    /**
     * The path <code>test/$engine/$process/pkg/$processId.zip</code>
     *
     * @return the path <code>test/$engine/$process/pkg/$processId.zip</code>
     */
    public Path getTargetPackageFilePath() {
        return getTargetPackageFilePath("zip");
    }

    public Path getTargetPackageFilePath(final String extension) {
        return getTargetPackagePath().resolve(getName() + "." + extension);
    }

    /**
     * The path <code>test/$engine/$process/pkg/$processId.jar</code>
     *
     * @return the path <code>test/$engine/$process/pkg/$processId.jar</code>
     */
    public Path getTargetPackageJarFilePath() {
        return getTargetPackageFilePath("jar");
    }

    /**
     * The path <code>test/$engine/$process/pkg/${processId}Application.zip</code>
     *
     * @return the path <code>test/$engine/$process/pkg/${processId}Application.zip</code>
     */
    public Path getTargetPackageCompositeFilePath() {
        return getTargetPackagePath().resolve(getTargetPackageCompositeFile());
    }

    /**
     * The file name <code>${processId}Application.zip</code>
     *
     * @return the file name <code>${processId}Application.zip</code>
     */
    public String getTargetPackageCompositeFile() {
        return getName() + "Application.zip";
    }

    /**
     * The path <code>test/$engine/$process/soapui</code>
     *
     * @return the path <code>test/$engine/$process/soapui</code>
     */
    public Path getTargetSoapUIPath() {
        return getTargetPath().resolve("soapui");
    }

    public String getTargetSoapUIProjectName() {
        return getEngine() + "." + getGroup().getName() + "." + getName();
    }

    /**
     * The file name <code>test_$engine_$process_soapui.xml</code>
     *
     * @return the file name <code>test_$engine_$process_soapui.xml</code>
     */
    public String getTargetSoapUIFileName() {
        return "test_" + getEngine() + "_" + getGroup().getName() + "_" + getName() + "_soapui.xml";
    }

    public Path getTargetSoapUIFilePath() {
        return getTargetSoapUIPath().resolve(getTargetSoapUIFileName());
    }

    public List<Path> getWsdlPaths() {
        return getWsdls();
    }

    public List<Path> getTargetWsdlPaths() {
        return getWsdlFileNames().stream().map((p) -> getTargetProcessPath().resolve(p)).collect(Collectors.toList());
    }

    public List<String> getWsdlFileNames() {
        return getWsdls().stream().map((p) -> p.getFileName().toString()).collect(Collectors.toList());
    }

    public List<Path> getAdditionalFilePaths() {
        return test.getFiles().stream().filter(p -> !p.toString().endsWith(".wsdl")).collect(Collectors.toList());
    }

    public String getShortId() {
        return new BPELIdShortener(getName()).getShortenedId();
    }

    public List<Path> getWsdls() {
        return test.getFiles().stream().filter(p -> p.toString().endsWith(".wsdl")).collect(Collectors.toList());
    }

    @Override
    public AbstractBPELEngine getEngine() {
        return engine;
    }

    @Override
    public Path getProcess() {
        return test.getProcess();
    }

    @Override
    public String getGroupName() {
        return getFeatureDimension().getGroup().getName();
    }

    @Override
    public Group getGroup() {
        return getFeatureDimension().getGroup();
    }

    @Override
    public Feature getFeature() {
        return test.getFeature();
    }

    @Override
    public int compareTo(BPELProcess o) {
        return test.compareTo(o.test);
    }

    public void setEngine(AbstractBPELEngine engine) {
        this.engine = engine;
    }

    public List<TestCase> getTestCases() {
        return test.getTestCases();
    }

    public void setTestCases(List<TestCase> testCases) {
        test = test.withNewTestCases(testCases);
    }

    public FeatureDimension getFeatureDimension() {
        return test;
    }

    @Override
    public EngineExtended getEngineObject() {
        return getEngine().getEngineObject();
    }
}

