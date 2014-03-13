package betsy

import betsy.data.BPMNProcess
import betsy.data.BPMNTestSuite
import betsy.data.engines.BPMNEngine
import betsy.executables.BPMNComposite

class BPMNBetsy {
    List<BPMNEngine> engines = []
    List<BPMNProcess> processes = []

    BPMNComposite composite = new BPMNComposite()

    public void execute() throws Exception {
        //new Validator(processes: processes).validate()

        BPMNTestSuite testSuite = BPMNTestSuite.createTests(engines, processes.sort())

        composite.testSuite = testSuite
        composite.execute()
    }
}
