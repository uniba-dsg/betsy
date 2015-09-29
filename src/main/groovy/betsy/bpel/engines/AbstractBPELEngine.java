package betsy.bpel.engines;

import betsy.bpel.model.BPELProcess;
import betsy.common.HasPath;
import betsy.common.engines.EngineAPI;
import betsy.common.engines.ProcessLanguage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBPELEngine implements EngineAPI<BPELProcess>, HasPath {

    private final List<BPELProcess> processes = new ArrayList<>();
    private BPELEnginePackageBuilder packageBuilder = new BPELEnginePackageBuilder();
    private Path parentFolder;

    public abstract Path getXsltPath();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractBPELEngine engine = (AbstractBPELEngine) o;

        return getName().equals(engine.getName());
    }

    @Override
    public ProcessLanguage getSupportedLanguage() {
        return ProcessLanguage.BPEL;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public BPELEnginePackageBuilder getPackageBuilder() {
        return packageBuilder;
    }

    public void setPackageBuilder(BPELEnginePackageBuilder packageBuilder) {
        this.packageBuilder = packageBuilder;
    }

    public Path getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Path parentFolder) {
        this.parentFolder = parentFolder;
    }

    public final List<BPELProcess> getProcesses() {
        return processes;
    }
}
