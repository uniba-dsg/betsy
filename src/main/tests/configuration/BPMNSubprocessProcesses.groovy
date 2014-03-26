package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

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

//    public static final BPMNProcess ADHOC = builder.buildSubprocessProcess(
//            "AdHoc", "de.uniba.dsg", "1.0", "A simple test for a Ad-Hoc subprocess",
//            [
//                    new BPMNTestCase(1).buildSimple()
//            ]
//    )

    public static final List<BPMNProcess> SUBPROCESSES = [
            SUBPROCESS,
            TRANSACTION//,
            //ADHOC
    ].flatten() as List<BPMNProcess>
}
