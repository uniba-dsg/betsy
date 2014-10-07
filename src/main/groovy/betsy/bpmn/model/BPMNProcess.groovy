package betsy.bpmn.model

import betsy.bpmn.engines.BPMNEngine

import java.nio.file.Path
import java.nio.file.Paths

class BPMNProcess implements Cloneable, Comparable {

    String description = ""

    String name
    String group

    String groupId = "de.uniba.dsg"
    String version = "1.0"

    List<BPMNTestCase> testCases = []

    BPMNEngine engine

    @Override
    protected Object clone(){
        return new BPMNProcess(name: name, group: group, groupId: groupId, version: version, engine: engine, testCases: testCases)
    }

    @Override
    public String toString() {
        return getNormalizedId()
    }

    String getName(){
        name
    }

    String getGroup(){
        group
    }

    String getGroupId(){
        groupId
    }

    String getVersion(){
        version
    }

    BPMNEngine getEngine(){
        engine
    }

    void setEngine(BPMNEngine engine){
        this.engine = engine
    }

    void setTestCases(List<BPMNTestCase> testCases){
        this.testCases = testCases
    }

    /**
     * An id as "language_features/structured_activities/Sequence" is transformed to
     * "language_features__structured_activities__Sequence"
     *
     * @return
     */
    String getNormalizedId() {
        "${group}__${name}"
    }

    Path getResourcePath(){
        Paths.get("src/main/tests/files/bpmn").resolve(group)
    }

    /**
     * The path <code>test/$engine/$process</code>
     *
     * @return the path <code>test/$engine/$process</code>
     */
    Path getTargetPath() {
        engine.path.resolve(normalizedId)
    }

    Path getTargetLogsPath() {
        targetPath.resolve("logs")
    }

    /**
     * The path <code>test/$engine/$process/reports</code>
     *
     * @return the path <code>test/$engine/$process/reports</code>
     */
    Path getTargetReportsPath() {
        targetPath.resolve("reports")
    }

    Path getTargetReportsPathWithCase(int c) {
        targetPath.resolve("reports").resolve("case" + c)
    }

    Path getTargetTestBinPath(){
        targetPath.resolve("testBin")
    }

    Path getTargetTestBinPathWithCase(int c){
        targetPath.resolve("testBin").resolve("case" + c)
    }

    Path getTargetTestSrcPath(){
        targetPath.resolve("testSrc")
    }

    Path getTargetTestSrcPathWithCase(int c){
        targetPath.resolve("testSrc").resolve("case" + c)
    }

    List<BPMNTestCase> getTestCases(){
        testCases
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BPMNProcess that = (BPMNProcess) o

        if (this.normalizedId != that.normalizedId) return false

        return true
    }

    int hashCode() {
        return normalizedId.hashCode()
    }

    @Override
    int compareTo(Object o) {
        return normalizedId.compareTo(((BPMNProcess)o).normalizedId)
    }
}
