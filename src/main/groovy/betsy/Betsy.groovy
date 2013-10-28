package betsy

import betsy.data.Engine
import betsy.data.BetsyProcess
import betsy.data.TestSuite
import betsy.executables.Composite

import betsy.executables.ExecutionContext
import betsy.executables.Validator

class Betsy {

    List<Engine> engines
    List<BetsyProcess> processes

    Composite composite = new Composite()

    public void execute() throws Exception {
        new Validator(processes: processes).validate()

        TestSuite tests = TestSuite.createTests(engines, processes.sort { it.id })

        ExecutionContext context = tests.buildExecutionContext()

        composite.context = context
        composite.execute()
    }
}
