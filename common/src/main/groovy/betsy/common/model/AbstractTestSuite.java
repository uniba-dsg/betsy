package betsy.common.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import betsy.common.engines.EngineAPI;

public abstract class AbstractTestSuite<E extends EngineAPI<P>,P extends ProcessFolderStructure> implements TestSuiteFolderStructure {

    /**
     * Base directory for the whole test suite.
     */
    private Path path;

    /**
     * List of engines to be tested in this test suite. The engines contain their own TestCases.
     */
    private List<E> engines = new ArrayList<>();

    private int processesCount = 0;

    public int getProcessesCount() {
        return processesCount;
    }

    public void setProcessesCount(int processesCount) {
        this.processesCount = processesCount;
    }

    public List<E> getEngines() {
        return engines;
    }

    public void setEngines(List<E> engines) {
        this.engines = engines;
    }

    @Override
    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return getPath().toString();
    }
}
