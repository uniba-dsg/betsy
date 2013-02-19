package betsy

import betsy.data.Engine
import betsy.data.Process
import betsy.data.TestSuite
import betsy.executables.Composite
import betsy.executables.ExecutionContext
import betsy.executables.Validator

class Betsy {

    List<Engine> engines
    List<Process> processes

    Composite composite = new Composite()

    public void execute() throws Exception {
        new Validator().validate(processes)

        TestSuite tests = TestSuite.createTests(engines, processes.sort { it.id })

        ExecutionContext context = tests.buildExecutionContext()

        composite.context = context
        composite.execute()
    }
}
