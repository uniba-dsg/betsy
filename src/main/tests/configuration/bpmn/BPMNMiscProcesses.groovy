package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNMiscProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess PARTICIPANT = builder.buildMiscProcess(
            "Participant", "A collaboration with a single participant",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )

    public static final BPMNProcess LANES = builder.buildMiscProcess(
            "Lanes", "A collaboration with a single participant with two lanes. Lanes have no effect on the " +
            "execution and should be ignored.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess CALL_ACTIVITY_PROCESS = builder.buildMiscProcess(
            "CallActivityProcess", "A collaboration with two participants. One process calls the other one through a callActivity.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess CALL_ACTIVITY_GLOBAL_TASK = builder.buildMiscProcess(
            "CallActivityGlobalTask", "Definitions contains a GlobalScriptTask which is called by a CallActivity.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final List<BPMNProcess> MISCS = [
            PARTICIPANT,
            LANES,
            CALL_ACTIVITY_PROCESS,
            CALL_ACTIVITY_GLOBAL_TASK
    ].flatten() as List<BPMNProcess>
}
