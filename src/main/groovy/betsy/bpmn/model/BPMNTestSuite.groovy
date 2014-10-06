package betsy.bpmn.model

import ant.tasks.AntUtil
import betsy.bpmn.engines.BPMNEngine

import java.nio.file.Path
import java.nio.file.Paths

class BPMNTestSuite {
    AntBuilder ant = AntUtil.builder()

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static BPMNTestSuite createTests(List<BPMNEngine> engines, List<BPMNProcess> processes) {
        BPMNTestSuite test = new BPMNTestSuite(path: Paths.get("test"))

        engines.each { engine ->
            engine.processes.addAll(processes.collect() { p -> p.clone() as BPMNProcess })
            // set engine
            engine.processes.each { process -> process.engine = engine }
            // set parentFolder
            engine.parentFolder = test.path
        }

        test.engines = engines

        return test
    }

    /**
     * Base directory for the whole test suite.
     */
    Path path

    /**
     * List of engines to be tested in this test suite. The engines contain their own TestCases.
     */
    List<BPMNEngine> engines = []


    Path getReportsPath() {
        path.resolve("reports")
    }

    static String getCsvFile() {
        "results.csv"
    }

    Path getCsvFilePath() {
        reportsPath.resolve(csvFile)
    }

    static String getCsvDurationFile() {
        "durations.csv"
    }

    Path getCsvDurationFilePath() {
        reportsPath.resolve(csvDurationFile)
    }

    Path getJUnitXMLFilePath() {
        reportsPath.resolve("TESTS-TestSuites.xml")
    }

    int getProcessesCount() {
        int result = 0;

        for (BPMNEngine engine : engines) {
            result += engine.processes.size()
        }

        return result;
    }

    @Override
    public String toString() {
        getPath()
    }
}
