package betsy.bpmn.model;

import betsy.bpmn.engines.BPMNEngine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BPMNTestSuite {
    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static BPMNTestSuite createTests(List<BPMNEngine> engines, final List<BPMNProcess> processes) {

        BPMNTestSuite test = new BPMNTestSuite();
        test.setPath(Paths.get("test"));

        for (BPMNEngine engine : engines) {
            List<BPMNProcess> clonedProcesses = processes.stream().map(p -> (BPMNProcess) p.clone()).collect(Collectors.toList());

            // link them
            for (BPMNProcess process : clonedProcesses) {
                process.setEngine(engine);
                engine.getProcesses().add(process);
            }

            // set parentFolder
            engine.setParentFolder(test.path);
        }


        test.engines = engines;

        return test;
    }

    public Path getReportsPath() {
        return path.resolve("reports");
    }

    public static String getCsvFile() {
        return "results.csv";
    }

    public Path getCsvFilePath() {
        return getReportsPath().resolve(getCsvFile());
    }

    public static String getCsvDurationFile() {
        return "durations.csv";
    }

    public Path getCsvDurationFilePath() {
        return getReportsPath().resolve(getCsvDurationFile());
    }

    public Path getJUnitXMLFilePath() {
        return getReportsPath().resolve("TESTS-TestSuites.xml");
    }

    public int getProcessesCount() {
        int result = 0;

        for (BPMNEngine engine : getEngines()) {
            result += engine.getProcesses().size();
        }


        return result;
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

    public List<BPMNEngine> getEngines() {
        return engines;
    }

    public void setEngines(List<BPMNEngine> engines) {
        this.engines = engines;
    }

    /**
     * Base directory for the whole test suite.
     */
    private Path path;
    /**
     * List of engines to be tested in this test suite. The engines contain their own TestCases.
     */
    private List<BPMNEngine> engines = new ArrayList<>();
}
