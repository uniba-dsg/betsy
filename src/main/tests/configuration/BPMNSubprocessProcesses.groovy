package configuration

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNSubprocessProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SUBPROCESS = builder.buildSubprocessProcess(
            "Subprocess", "de.uniba.dsg", "1.0", "A simple test for a subprocess",
            [
                    new BPMNTestCase(1).assertSuccess().assertSubprocess()
            ]
    )

    public static final BPMNProcess TRANSACTION = builder.buildSubprocessProcess(
            "Transaction", "de.uniba.dsg", "1.0", "A simple test for a transaction subprocess",
            [
                    new BPMNTestCase(1).assertTransactionTask().assertSuccess()
            ]
    )

    public static final List<BPMNProcess> SUBPROCESSES = [
            SUBPROCESS,
            TRANSACTION
    ].flatten() as List<BPMNProcess>
}
