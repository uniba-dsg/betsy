package betsy.data.engines

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.tasks.FileTasks

import java.nio.file.Path
import java.nio.file.Paths

abstract class Engine implements EngineAPI {

    AntBuilder ant = AntUtil.builder()
	EnginePackageBuilder packageBuilder = new EnginePackageBuilder()

    Path parentFolder

    final List<BetsyProcess> processes = []

    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    Path getXsltPath() {
        Paths.get(Engine.class.getResource("/" + name).toURI())
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

        Engine engine = (Engine) o

        if (getName() != engine.getName()) return false

        return true
    }

    int hashCode() {
        return (getName() != null ? getName().hashCode() : 0)
    }
	
}