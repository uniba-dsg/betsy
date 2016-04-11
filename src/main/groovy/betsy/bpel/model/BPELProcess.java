package betsy.bpel.model;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.HasPath;
import betsy.common.model.AbstractProcess;
import betsy.common.model.EngineIndependentProcess;
import betsy.common.model.ProcessFolderStructure;
import betsy.common.model.TestCase;
import betsy.common.model.feature.Group;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BPELProcess implements ProcessFolderStructure, Comparable<BPELProcess>  {

    private EngineIndependentProcess engineIndependentProcess;
    private AbstractBPELEngine engine;

    public BPELProcess(EngineIndependentProcess engineIndependentProcess) {
        this.engineIndependentProcess = Objects.requireNonNull(engineIndependentProcess);
    }

    public BPELProcess createCopyWithoutEngine() {
        return new BPELProcess(engineIndependentProcess);
    }

    public String getEndpoint() {
        return getEngine().getEndpointUrl(this);
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
        return getEngine() + "." + getGroup() + "." + getName();
    }

    /**
     * The file name <code>test_$engine_$process_soapui.xml</code>
     *
     * @return the file name <code>test_$engine_$process_soapui.xml</code>
     */
    public String getTargetSoapUIFileName() {
        return "test_" + getEngine() + "_" + getGroup() + "_" + getName() + "_soapui.xml";
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
    public String getGroup() {
        return getGroupObject().getName();
    }

    @Override
    public Group getGroupObject() {
        return engineIndependentProcess.getGroup();
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
        engineIndependentProcess.setTestCases(testCases);
    }
}

