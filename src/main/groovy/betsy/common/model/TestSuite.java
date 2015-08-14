package betsy.common.model;

import betsy.common.engines.EngineAPI;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestSuite<E extends EngineAPI<P>,P extends ProcessFolderStructure> {
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

    public static String getCsvFile() {
        return "results.csv";
    }

    public static String getCsvDurationFile() {
        return "durations.csv";
    }

    public Path getReportsPath() {
        return path.resolve("reports");
    }

    public Path getCsvFilePath() {
        return getReportsPath().resolve(getCsvFile());
    }

    public Path getCsvDurationFilePath() {
        return path.resolve(getCsvDurationFile());
    }

    public Path getJUnitXMLFilePath() {
        return getReportsPath().resolve("TESTS-TestSuites.xml");
    }

    @Override
    public String toString() {
        return getPath().toString();
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public List<E> getEngines() {
        return engines;
    }

    public void setEngines(List<E> engines) {
        this.engines = engines;
    }
}
