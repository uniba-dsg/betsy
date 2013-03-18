package betsy.data

import betsy.executables.ExecutionContext

class TestSuite {

    AntBuilder ant = new AntBuilder()

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static TestSuite createTests(List<Engine> engines, List<Process> processes) {
        TestSuite test = new TestSuite(path: "test")

        engines.each { engine ->
            engine.processes.addAll(processes.collect() { p -> p.clone() as Process})
            engine.processes.each { process -> process.engine = engine}
            engine.testSuite = test
        }

        test.engines = engines

        return test
    }

    /**
     * Base directory for the whole test suite.
     */
    String path

    /**
     * List of engines to be tested in this test suite. The engines contain their own TestCases.
     */
    List<Engine> engines = []


    String getReportsPath() {
        "${getPath()}/reports"
    }

    String getCsvFile() {
        "results.csv"
    }

    String getCsvFilePath() {
        "${getReportsPath()}/${getCsvFile()}"
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
