package betsy.common.model;

import betsy.bpel.engines.Engine;
import betsy.bpel.model.BetsyProcess;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestSuite {
    /**
     * Base directory for the whole test suite.
     */
    private Path path;
    /**
     * List of engines to be tested in this test suite. The engines contain their own TestCases.
     */
    private List<Engine> engines = new ArrayList<>();

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static TestSuite createTests(List<Engine> engines, List<BetsyProcess> processes) {
        TestSuite test = new TestSuite();
        test.setPath(Paths.get("test"));

        for (Engine engine : engines) {
            List<BetsyProcess> clonedProcesses = processes.stream().map(p -> (BetsyProcess) p.clone()).collect(Collectors.toList());

            // link them
            for (BetsyProcess process : clonedProcesses) {
                process.setEngine(engine);
                engine.getProcesses().add(process);
            }

            // set parentFolder
            engine.setParentFolder(test.path);
        }


        test.engines = engines;

        return test;
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
        return getReportsPath().resolve(getCsvDurationFile());
    }

    public Path getJUnitXMLFilePath() {
        return getReportsPath().resolve("TESTS-TestSuites.xml");
    }

    public int getProcessesCount() {
        return (int) engines.stream().map(e -> e.getProcesses().size()).count();
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

    public List<Engine> getEngines() {
        return engines;
    }

    public void setEngines(List<Engine> engines) {
        this.engines = engines;
    }
}
