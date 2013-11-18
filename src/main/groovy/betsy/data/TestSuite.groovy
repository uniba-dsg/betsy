package betsy.data

import ant.tasks.AntUtil
import betsy.data.engines.Engine
import betsy.executables.ExecutionContext

import java.nio.file.Path
import java.nio.file.Paths

class TestSuite {

    AntBuilder ant = AntUtil.builder()

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static TestSuite createTests(List<Engine> engines, List<BetsyProcess> processes) {
        TestSuite test = new TestSuite(path: Paths.get("test"))

        engines.each { engine ->
            engine.processes.addAll(processes.collect() { p -> p.clone() as BetsyProcess })
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
    List<Engine> engines = []


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

        for (Engine engine : engines) {
            result += engine.processes.size()
        }

        return result;
    }

    public void prepare() {
        ant.delete dir: path
        ant.mkdir dir: path
    }


    @Override
    public String toString() {
        getPath()
    }

    public ExecutionContext buildExecutionContext() {
        new ExecutionContext(testSuite: this)
    }

}
