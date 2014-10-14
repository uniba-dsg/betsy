package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNMiscProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess POOL = builder.buildMiscProcess(
            "Pool", "A collaboration with a single pool",
            [
                    new BPMNTestCase(1).assertSuccess()
            ]
    )

    public static final BPMNProcess LANES = builder.buildMiscProcess(
            "Lanes", "A collaboration with a single pool with two lanes",
            [
                    new BPMNTestCase(1).assertLane1().assertLane2().assertSuccess()
            ]
    )

    public static final BPMNProcess CALL_ACTIVITY = builder.buildMiscProcess(
            "CallActivity", "A collaboration with two pools. One process calls the other one through a callActivity",
            [
                    new BPMNTestCase(1).assertCallableElementExecuted().assertSuccess()
            ]
    )

    public static final BPMNProcess CALL_ACTIVITY_GLOBAL_TASK = builder.buildMiscProcess(
            "CallActivityGlobalTask", "A collaboration with a single pool. The pool contains a callActivity that calls a globalScriptTask",
            [
                    new BPMNTestCase(1).assertCallableElementExecuted().assertSuccess()
            ]
    )

    public static final List<BPMNProcess> MISCS = [
            POOL,
            LANES,
            CALL_ACTIVITY,
            CALL_ACTIVITY_GLOBAL_TASK
    ].flatten() as List<BPMNProcess>
}
