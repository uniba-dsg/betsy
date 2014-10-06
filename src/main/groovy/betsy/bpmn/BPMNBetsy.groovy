package betsy.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestSuite
import betsy.bpmn.engines.BPMNEngine

class BPMNBetsy {
    List<BPMNEngine> engines = []
    List<BPMNProcess> processes = []

    BPMNComposite composite = new BPMNComposite()

    public void execute() throws Exception {
        BPMNTestSuite testSuite = BPMNTestSuite.createTests(engines, processes.sort())

        composite.testSuite = testSuite
        composite.execute()
    }
}
