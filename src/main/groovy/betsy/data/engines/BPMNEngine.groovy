package betsy.data.engines

import ant.tasks.AntUtil
import betsy.data.BPMNProcess

import java.nio.file.Path
import java.nio.file.Paths

abstract class BPMNEngine implements BPMNEngineAPI{
    AntBuilder ant = AntUtil.builder()

    Path parentFolder

    final List<BPMNProcess> processes = []

    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    Path getXsltPath() {
        Paths.get(BPMNEngine.class.getResource("/" + name).toURI())
    }

    /**
     * The path <code>test/$engine</code>
     *
     * @return the path <code>test/$engine</code>
     */
    Path getPath() {
        parentFolder.resolve(name)
    }

    @Override
    public String toString() {
        return getName();
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BPMNEngine engine = (BPMNEngine) o

        if (getName() != engine.getName()) return false

        return true
    }

    int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
    }
}
