package betsy

import betsy.data.BetsyProcess
import betsy.data.TestSuite
import betsy.data.engines.Engine;
import betsy.executables.Composite

import betsy.executables.Validator

class Betsy {

    List<Engine> engines
    List<BetsyProcess> processes

    Composite composite = new Composite()

    public void execute() throws Exception {
        new Validator(processes: processes).validate()

        TestSuite testSuite = TestSuite.createTests(engines, processes.sort { it.id })

        composite.testSuite = testSuite
        composite.execute()
    }
}
