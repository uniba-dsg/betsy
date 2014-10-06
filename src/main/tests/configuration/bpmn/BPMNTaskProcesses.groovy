package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNTaskProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SIMPLE = builder.buildTaskProcess(
            "SequenceFlow", "A Test for the basic process using a script task",
            [
                    new BPMNTestCase().assertSuccess()
            ]
    )

    public static final BPMNProcess SEQUENCE_CONDITIONAL = builder.buildTaskProcess(
            "SequenceFlowConditional", "A Test for a conditional sequence flow",
            [
                    new BPMNTestCase(1).inputA().assertSuccess().assertCondition(),
                    new BPMNTestCase(2).inputB().assertSuccess()
            ]
    )

    public static final BPMNProcess SEQUENCE_CONDITIONAL_DEFAULT = builder.buildTaskProcess(
            "SequenceFlowConditionalDefault", "A Test for a conditional and a default sequence flow",
            [
                    new BPMNTestCase(1).inputA().assertCondition(),
                    new BPMNTestCase(2).inputB().assertSuccess()
            ]
    )

    public static final BPMNProcess MULTI_SEQUENTIAL = builder.buildTaskProcess(
            "MultiSequencialTask", "A simple Test for a 3 times sequentially instantiated script task",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSuccess()
            ]
    )

    public static final BPMNProcess MULTI_SEQUENTIAL_NONE = builder.buildTaskProcess(
            "MultiSequencialTaskNoneBehavior", "A Test for a 3 times sequentially instantiated script task with a none multi instance behavior",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSignaled().assertSignaled().assertSignaled()
            ]
    )

    public static final BPMNProcess MULTI_SEQUENTIAL_ONE = builder.buildTaskProcess(
            "MultiSequencialTaskOneBehavior", "A Test for a 3 times sequentially instantiated script task with a one multi instance behavior",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSignaled()
            ]
    )

    public static final BPMNProcess MULTI_PARALLEL = builder.buildTaskProcess(
            "MultiParallelTask", "A simple Test for a 3 times parallel instantiated script task",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSuccess()
            ]
    )

    public static final BPMNProcess LOOP = builder.buildTaskProcess(
            "LoopTask", "A simple Test for a 3 times looped script task",
            [
                    new BPMNTestCase().assertMulti().assertMulti().assertMulti().assertSuccess()
            ]
    )

    public static final List<BPMNProcess> TASKS = [
            SIMPLE,
            SEQUENCE_CONDITIONAL,
            SEQUENCE_CONDITIONAL_DEFAULT,
            MULTI_SEQUENTIAL,
            MULTI_SEQUENTIAL_NONE,
            MULTI_SEQUENTIAL_ONE,
            MULTI_PARALLEL,
            LOOP
    ].flatten() as List<BPMNProcess>
}
