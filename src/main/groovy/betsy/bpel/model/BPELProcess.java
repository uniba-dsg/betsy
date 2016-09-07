package betsy.bpel.model;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.common.model.engine.Engine;
import betsy.common.model.engine.EngineDimension;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.ProcessFolderStructure;
import betsy.common.model.input.TestCase;
import betsy.common.model.feature.Feature;
import betsy.common.model.feature.FeatureDimension;
import betsy.common.model.feature.Group;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BPELProcess implements ProcessFolderStructure, Comparable<BPELProcess>, FeatureDimension, EngineDimension {

    private EngineIndependentProcess engineIndependentProcess;
    private AbstractBPELEngine engine;
    private Path deploymentPackagePath;

    public BPELProcess(EngineIndependentProcess engineIndependentProcess) {
        this.engineIndependentProcess = Objects.requireNonNull(engineIndependentProcess);
    }

    public BPELProcess createCopyWithoutEngine() {
        return new BPELProcess(engineIndependentProcess);
    }

    public EngineIndependentProcess getEngineIndependentProcess() {
        return engineIndependentProcess;
    }

    public void setEngineIndependentProcess(EngineIndependentProcess engineIndependentProcess) {
        this.engineIndependentProcess = engineIndependentProcess;
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
        return engineIndependentProcess.getFiles().stream().filter(p -> !p.toString().endsWith(".wsdl")).collect(Collectors.toList());
    }

    public String getShortId() {
        return new BPELIdShortener(getName()).getShortenedId();
    }

    public List<Path> getWsdls() {
        return engineIndependentProcess.getFiles().stream().filter(p -> p.toString().endsWith(".wsdl")).collect(Collectors.toList());
    }

    @Override
    public AbstractBPELEngine getEngine() {
        return engine;
    }

    @Override
    public Path getProcess() {
        return engineIndependentProcess.getProcess();
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
        return engineIndependentProcess.getFeature();
    }

    @Override
    public int compareTo(BPELProcess o) {
        return engineIndependentProcess.compareTo(o.engineIndependentProcess);
    }

    public void setEngine(AbstractBPELEngine engine) {
        this.engine = engine;
    }

    public List<TestCase> getTestCases() {
        return engineIndependentProcess.getTestCases();
    }

    public void setTestCases(List<TestCase> testCases) {
        engineIndependentProcess = engineIndependentProcess.withNewTestCases(testCases);
    }

    public FeatureDimension getFeatureDimension() {
        return engineIndependentProcess;
    }

    @Override
    public Engine getEngineObject() {
        return getEngine().getEngineObject();
    }
}

