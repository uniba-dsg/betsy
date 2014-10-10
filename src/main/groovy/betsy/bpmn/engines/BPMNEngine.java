package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNProcess;
import betsy.common.engines.EngineAPI;
import betsy.common.engines.LocalEngineAPI;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class BPMNEngine implements EngineAPI<BPMNProcess>, LocalEngineAPI {
    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/" + getName());
    }

    /**
     * The path <code>test/$engine</code>
     *
     * @return the path <code>test/$engine</code>
     */
    public Path getPath() {
        return parentFolder.resolve(getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!getClass().equals(o.getClass())) return false;

        BPMNEngine engine = (BPMNEngine) o;

        return getName().equals(engine.getName());
    }

    public int hashCode() {
        return (getName() != null ? getName().hashCode() : 0);
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
     *
     * @param process
     */
    public abstract void buildTest(BPMNProcess process);

    /**
     * performs test for the BPMN Process
     *
     * @param process
     */
    public abstract void testProcess(BPMNProcess process);

    public Path getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Path parentFolder) {
        this.parentFolder = parentFolder;
    }

    public final List<BPMNProcess> getProcesses() {
        return processes;
    }

    private Path parentFolder;
    private final List<BPMNProcess> processes = new ArrayList<BPMNProcess>();
}
