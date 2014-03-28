package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNTaskProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SIMPLE = builder.buildTaskProcess(
            "SequenceFlow", "de.uniba.dsg", "1.0", "A Test for the basic process using a script task",
            [
                    new BPMNTestCase().assertSuccess()
            ]
    )

    public static final BPMNProcess SEQUENCE_CONDITIONAL = builder.buildTaskProcess(
            "SequenceFlowConditional", "de.uniba.dsg", "1.0", "A Test for a conditional sequence flow",
            [
                    new BPMNTestCase(1).inputA().assertSuccess().assertCondition(),
                    new BPMNTestCase(2).inputB().assertSuccess()
            ]
    )

    public static final BPMNProcess SEQUENCE_CONDITIONAL_DEFAULT = builder.buildTaskProcess(
            "SequenceFlowConditionalDefault", "de.uniba.dsg", "1.0", "A Test for a conditional and a default sequence flow",
            [
                    new BPMNTestCase(1).inputA().assertCondition(),
                    new BPMNTestCase(2).inputB().assertSuccess()
            ]
    )

    public static final BPMNProcess MULTI_SEQUENTIAL = builder.buildTaskProcess(
            "MultiSequencialTask", "de.uniba.dsg", "1.0", "A simple Test for a 3 times sequentially instantiated script task",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSuccess()
            ]
    )

    public static final BPMNProcess MULTI_PARALLEL = builder.buildTaskProcess(
            "MultiParallelTask", "de.uniba.dsg", "1.0", "A simple Test for a 3 times parallel instantiated script task",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSuccess()
            ]
    )

    public static final BPMNProcess LOOP = builder.buildTaskProcess(
            "LoopTask", "de.uniba.dsg", "1.0", "A simple Test for a 3 times looped script task",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSuccess()
            ]
    )

    public static final List<BPMNProcess> TASKS = [
            SIMPLE,
            SEQUENCE_CONDITIONAL,
            SEQUENCE_CONDITIONAL_DEFAULT,
            MULTI_SEQUENTIAL,
            MULTI_PARALLEL,
            LOOP
    ].flatten() as List<BPMNProcess>
}
