package betsy.bpel.model;

import betsy.bpel.engines.Engine;
import betsy.common.engines.Nameable;
import betsy.common.model.TestCase;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BPELProcess implements Cloneable, Comparable, Nameable {
    public void setTestCases(List<TestCase> testCases) {
        uniqueifyTestCaseNames(testCases);
        this.testCases = testCases;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    private static void uniqueifyTestCaseNames(List<TestCase> testCases) {
        // group by name of test case
        for (int counter = 1; counter < testCases.size(); counter++) {
            TestCase testCase = testCases.get(counter - 1);
            testCase.setName(testCase.getName() + "-" + counter);
        }
    }

    @Override
    public Object clone() {
        BPELProcess process = new BPELProcess();
        process.setBpel(bpel);
        process.setWsdls(wsdls);
        process.setAdditionalFiles(additionalFiles);
        process.setTestCases(testCases);
        return process;
    }

    @Override
    public String toString() {
        return getNormalizedId();
    }

    public String getEndpoint() {
        return getEngine().getEndpointUrl(this);
    }

    public String getWsdlEndpoint() {
        return getEndpoint() + "?wsdl";
    }

    public String getGroup() {
        return bpel.getParent().getFileName().toString();
    }

    /**
     * An id as "language_features/structured_activities/Sequence" is transformed to
     * "language_features__structured_activities__Sequence"
     *
     * @return
     */
    public String getNormalizedId() {
        return getGroup() + "__" + getName();
    }

    /**
     * A bpel path as "language_features/structured_activities/Sequence.bpel" is used to extract "Sequence.bpel"
     *
     * @return
     */
    public String getBpelFileName() {
        return bpel.getFileName().toString();
    }

    public String getName() {
        return getBpelFileNameWithoutExtension();
    }

    public String getBpelFileNameWithoutExtension() {
        return getBpelFileName().substring(0, getBpelFileName().length() - 5);
    }

    public Path getBpelFilePath() {
        return bpel;
    }

    /**
     * The path <code>test/$engine/$process</code>
     *
     * @return the path <code>test/$engine/$process</code>
     */
    public Path getTargetPath() {
        return engine.getPath().resolve(getNormalizedId());
    }

    public Path getTargetLogsPath() {
        return getTargetPath().resolve("logs");
    }

    /**
     * The path <code>test/$engine/$process/pkg</code>
     *
     * @return the path <code>test/$engine/$process/pkg</code>
     */
    public Path getTargetPackagePath() {
        return getTargetPath().resolve("pkg");
    }

    /**
     * The path <code>test/$engine/$process/tmp</code>
     *
     * @return the path <code>test/$engine/$process/tmp</code>
     */
    public Path getTargetTmpPath() {
        return getTargetPath().resolve("tmp");
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

    /**
     * The path <code>test/$engine/$process/reports</code>
     *
     * @return the path <code>test/$engine/$process/reports</code>
     */
    public Path getTargetReportsPath() {
        return getTargetPath().resolve("reports");
    }

    public String getTargetSoapUIProjectName() {
        String result = engine + "." + getGroup() + "." + getName();
        return result.replaceAll("__", ".");
    }

    /**
     * The file name <code>test_$engine_$process_soapui.xml</code>
     *
     * @return the file name <code>test_$engine_$process_soapui.xml</code>
     */
    public String getTargetSoapUIFileName() {
        return "test_" + engine + "_" + getGroup() + "_" + getName() + "_soapui.xml";
    }

    public Path getTargetSoapUIFilePath() {
        return getTargetSoapUIPath().resolve(getTargetSoapUIFileName());
    }

    /**
     * The path <code>test/$engine/$process/bpel</code>
     *
     * @return the path <code>test/$engine/$process/bpel</code>
     */
    public Path getTargetBpelPath() {
        return getTargetPath().resolve("bpel");
    }

    public Path getTargetBpelFilePath() {
        return getTargetBpelPath().resolve(getBpelFileName());
    }

    public List<Path> getWsdlPaths() {
        return getWsdls();
    }

    public List<Path> getTargetWsdlPaths() {
        return getWsdlFileNames().stream().map((p) -> getTargetBpelPath().resolve(p)).collect(Collectors.toList());
    }

    public List<String> getWsdlFileNames() {
        return wsdls.stream().map((p) -> p.getFileName().toString()).collect(Collectors.toList());
    }

    public List<Path> getAdditionalFilePaths() {
        return additionalFiles;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!getClass().equals(o.getClass())) {
            return false;
        }

        BPELProcess that = (BPELProcess) o;

        return bpel.toString().equals(that.bpel.toString());

    }

    public int hashCode() {
        return bpel.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        return bpel.compareTo(((BPELProcess) o).bpel);
    }

    public String getShortId() {
        String name = getName();

        // short names are OK as they are
        if (name.length() < 8) {
            return name;
        }

        // abbreviate common names
        name = name.replaceAll("Receive", "REC");
        name = name.replaceAll("Rec", "REC");
        name = name.replaceAll("Request", "REQ");
        name = name.replaceAll("Req", "REQ");
        name = name.replaceAll("Reply", "REP");
        name = name.replaceAll("Invoke", "INV");

        name = name.replaceAll("Stop", "STP");

        name = name.replaceAll("ForEach", "FE");
        name = name.replaceAll("For", "FOR");

        return name.replaceAll("[a-z]", "");
    }

    public Path getBpel() {
        return bpel;
    }

    public void setBpel(Path bpel) {
        this.bpel = bpel;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    private Path bpel;
    private Engine engine;
    private String description = "";
    private List<TestCase> testCases = new ArrayList<>();
    private List<Path> wsdls = new ArrayList<>();
    /**
     * Additional files like xslt scripts or other resources.
     */
    private List<Path> additionalFiles = new ArrayList<>();
}
