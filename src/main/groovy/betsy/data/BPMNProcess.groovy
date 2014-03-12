package betsy.data

import betsy.data.engines.BPMNEngine

import java.nio.file.Path
import java.nio.file.Paths

class BPMNProcess implements Cloneable, Comparable {

    String description = ""

    String name
    String group

    String key
    String groupId
    String version

    BPMNEngine engine

    @Override
    protected Object clone(){
        return new BPMNProcess(name: name, group: group, key: key, groupId: groupId, version: version, engine: engine)
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

    String getKey(){
        key
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
        Paths.get("bpmnRes").resolve("files").resolve(group).resolve(name)
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

    Path getTargetTestBinPath(){
        targetPath.resolve("testBin")
    }

    Path getTargetTestSrcPath(){
        targetPath.resolve("testSrc")
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
