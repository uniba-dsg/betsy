package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNMiscProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess POOL = builder.buildMiscProcess(
            "Pool", "de.uniba.dsg", "1.0", "A test for a pool",
            [
                    new BPMNTestCase(1).buildSimple()
            ]
    )

    public static final BPMNProcess LANES = builder.buildMiscProcess(
            "Lanes", "de.uniba.dsg", "1.0", "A test for a pool with 2 lanes",
            [
                    new BPMNTestCase(1).buildTwoLanes()
            ]
    )

    public static final List<BPMNProcess> MISCS = [
            POOL,
            LANES
    ].flatten() as List<BPMNProcess>
}
