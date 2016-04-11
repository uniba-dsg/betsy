package betsy.bpel.model;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.common.model.AbstractProcess;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BPELProcess extends AbstractProcess<BPELTestCase, AbstractBPELEngine> {

    @Override
    public BPELProcess createCopyWithoutEngine() {
        BPELProcess process = new BPELProcess();
        process.setProcess(getProcess());
        process.setTestCases(getTestCases());
        process.setDescription(getDescription());
        process.setGroup(getGroupObject());

        process.setWsdls(wsdls);
        process.setAdditionalFiles(additionalFiles);

        return process;
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
        return wsdls.stream().map((p) -> p.getFileName().toString()).collect(Collectors.toList());
    }

    public List<Path> getAdditionalFilePaths() {
        return additionalFiles;
    }

    public String getShortId() {
        return new BPELIdShortener(getName()).getShortenedId();
    }

    public List<Path> getWsdls() {
        return wsdls;
    }

    public void setWsdls(List<Path> wsdls) {
        this.wsdls = wsdls;
    }

    public List<Path> getAdditionalFiles() {
        return additionalFiles;
    }

    public void setAdditionalFiles(List<Path> additionalFiles) {
        this.additionalFiles = additionalFiles;
    }

    private List<Path> wsdls = new ArrayList<>();
    /**
     * Additional files like xslt scripts or other resources.
     */
    private List<Path> additionalFiles = new ArrayList<>();


}

