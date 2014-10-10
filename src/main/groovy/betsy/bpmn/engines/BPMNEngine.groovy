package betsy.bpmn.engines

import betsy.bpmn.model.BPMNProcess
import betsy.common.engines.EngineAPI
import betsy.common.engines.LocalEngineAPI
import betsy.common.tasks.FileTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Path
import java.nio.file.Paths

abstract class BPMNEngine implements EngineAPI<BPMNProcess>, LocalEngineAPI {

    Path parentFolder

    final List<BPMNProcess> processes = []

    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/" + getName());
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

    @Override
    public Path getServerPath() {
        return Paths.get("server").resolve(getName());
    }

    @Override
    public boolean isInstalled() {
        return FileTasks.hasFolder(getServerPath());
    }

    @Override
    public void uninstall() {
        FileTasks.deleteDirectory(getServerPath());
    }

    /**
     * Builds test for the BPMN process
     * @param process
     */
    abstract void buildTest(BPMNProcess process);

    /**
     * performs test for the BPMN Process
     * @param process
     */
    abstract void testProcess(BPMNProcess process);
}
