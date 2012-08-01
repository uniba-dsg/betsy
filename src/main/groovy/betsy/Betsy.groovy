package betsy

import betsy.data.Engine
import betsy.data.Process
import betsy.data.TestSuite
import betsy.executables.Composite
import betsy.executables.ExecutionContext
import betsy.executables.Init
import betsy.executables.Validator

class Betsy {

    List<Engine> engines
    List<Process> processes

    Composite composite = new Composite()

    public void execute() {
        new Validator().validate(processes)

        new Init().allowInsecureDownloads()

        TestSuite tests = TestSuite.createTests(engines, processes.sort { it.id })

        ExecutionContext context = tests.buildExecutionContext()

        try {
            composite.context = context
            composite.execute()
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            // shutdown as SoapUI creates threads which cannot be shutdown so easily
            System.exit(0)
        }
    }
}
