package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNSubprocessProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SUBPROCESS = builder.buildSubprocessProcess(
            "Subprocess", "de.uniba.dsg", "1.0", "A simple test for a subprocess",
            [
                    new BPMNTestCase(1).buildSubprocess()
            ]
    )

    public static final List<BPMNProcess> SUBPROCESSES = [
            SUBPROCESS
    ].flatten() as List<BPMNProcess>
}
