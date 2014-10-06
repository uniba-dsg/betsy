package betsy.bpel.engines;

import ant.tasks.AntUtil;
import betsy.bpel.model.BetsyProcess;
import betsy.engines.EngineAPI;
import groovy.util.AntBuilder;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Engine implements EngineAPI<BetsyProcess> {

    private final List<BetsyProcess> processes = new ArrayList<>();
    private AntBuilder ant = AntUtil.builder();
    private EnginePackageBuilder packageBuilder = new EnginePackageBuilder();
    private Path parentFolder;

    /**
     * The path <code>src/main/xslt/$engine</code>
     *
     * @return the path <code>src/main/xslt/$engine</code>
     */
    public Path getXsltPath() {
        try {
            return Paths.get(Engine.class.getResource("/" + getName()).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("path not found", e);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Engine engine = (Engine) o;

        return getName().equals(engine.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public AntBuilder getAnt() {
        return ant;
    }

    public void setAnt(AntBuilder ant) {
        this.ant = ant;
    }

    public EnginePackageBuilder getPackageBuilder() {
        return packageBuilder;
    }

    public void setPackageBuilder(EnginePackageBuilder packageBuilder) {
        this.packageBuilder = packageBuilder;
    }

    public Path getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Path parentFolder) {
        this.parentFolder = parentFolder;
    }

    public final List<BetsyProcess> getProcesses() {
        return processes;
    }
}
