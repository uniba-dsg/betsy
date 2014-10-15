package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNSubprocessProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SUBPROCESS = builder.buildProcessWithSubProcess(
            "SubProcess", "A process that contains a subProcess",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess TRANSACTION = builder.buildProcessWithSubProcess(
            "Transaction", "A process that contains a transaction",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final List<BPMNProcess> SUBPROCESSES = [
            SUBPROCESS,
            TRANSACTION
    ].flatten() as List<BPMNProcess>
}
