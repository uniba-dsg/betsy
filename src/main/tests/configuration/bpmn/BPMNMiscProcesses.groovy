package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNMiscProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess POOL = builder.buildMiscProcess(
            "Pool", "de.uniba.dsg", "1.0", "A test for a pool",
            [
                    new BPMNTestCase(1).assertSuccess()
            ]
    )

    public static final BPMNProcess LANES = builder.buildMiscProcess(
            "Lanes", "de.uniba.dsg", "1.0", "A test for a pool with 2 lanes",
            [
                    new BPMNTestCase(1).assertLane1().assertLane2().assertSuccess()
            ]
    )

    public static final BPMNProcess CALL_ACTIVITY = builder.buildMiscProcess(
            "CallActivity", "de.uniba.dsg", "1.0", "A test for a call activity with two pools",
            [
                    new BPMNTestCase(1).assertCalled().assertSuccess()
            ]
    )

    public static final BPMNProcess CALL_ACTIVITY_GLOBAL_TASK = builder.buildMiscProcess(
            "CallActivityGlobalTask", "de.uniba.dsg", "1.0", "A test for a call activity with a global script task",
            [
                    new BPMNTestCase(1).assertCalled().assertSuccess()
            ]
    )

    public static final List<BPMNProcess> MISCS = [
            POOL,
            LANES,
            CALL_ACTIVITY,
            CALL_ACTIVITY_GLOBAL_TASK
    ].flatten() as List<BPMNProcess>
}
