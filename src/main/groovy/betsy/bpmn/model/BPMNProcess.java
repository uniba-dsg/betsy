package betsy.bpmn.model;

import betsy.bpmn.engines.BPMNEngine;
import betsy.common.engines.Nameable;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BPMNProcess implements Cloneable, Comparable, Nameable {

    @Override
    protected Object clone() {
        BPMNProcess process = new BPMNProcess();
        process.setName(name);
        process.setGroup(group);
        process.setGroupId(groupId);
        process.setVersion(version);
        process.setEngine(engine);
        process.setTestCases(testCases);

        return process;
    }

    @Override
    public String toString() {
        return getNormalizedId();
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    public BPMNEngine getEngine() {
        return engine;
    }

    public void setEngine(BPMNEngine engine) {
        this.engine = engine;
    }

    public void setTestCases(List<BPMNTestCase> testCases) {
        this.testCases = testCases;
    }

    /**
     * An id as "language_features/structured_activities/Sequence" is transformed to
     * "language_features__structured_activities__Sequence"
     *
     * @return the normalized id
     */
    public String getNormalizedId() {
        return group + "__" + name;
    }

    public Path getResourcePath() {
        return Paths.get("src/main/tests/files/bpmn").resolve(group);
    }

    public Path getResourceFile() {
        return Paths.get("src/main/tests/files/bpmn").resolve(group).resolve(name + ".bpmn");
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
     * The path <code>test/$engine/$process/reports</code>
     *
     * @return the path <code>test/$engine/$process/reports</code>
     */
    public Path getTargetReportsPath() {
        return getTargetPath().resolve("reports");
    }

    public Path getTargetReportsPathWithCase(int c) {
        return getTargetReportsPath().resolve("case" + c);
    }

    public Path getTargetTestBinPath() {
        return getTargetPath().resolve("testBin");
    }

    public Path getTargetTestBinPathWithCase(int c) {
        return getTargetTestBinPath().resolve("case" + c);
    }

    public Path getTargetTestSrcPath() {
        return getTargetPath().resolve("testSrc");
    }

    public Path getTargetTestSrcPathWithCase(int c) {
        return getTargetTestSrcPath().resolve("case" + c);
    }

    public List<BPMNTestCase> getTestCases() {
        return testCases;
    }

    public boolean equals(Object o) {
        if (DefaultGroovyMethods.is(this, o)) return true;
        if (!getClass().equals(o.getClass())) return false;

        BPMNProcess that = (BPMNProcess) o;

        return this.getNormalizedId().equals(that.getNormalizedId());
    }

    public int hashCode() {
        return getNormalizedId().hashCode();
    }

    @Override
    public int compareTo(Object o) {
        return getNormalizedId().compareTo(((BPMNProcess) o).getNormalizedId());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String description = "";
    private String name;
    private String group;
    private String groupId = "de.uniba.dsg";
    private String version = "1.0";
    private List<BPMNTestCase> testCases = new ArrayList<>();
    private BPMNEngine engine;
}
