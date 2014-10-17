package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNTaskProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SEQUENCE_FLOW = builder.buildTaskProcess(
            "SequenceFlow", "A process with two scriptTasks connected by a sequenceFlow",
            [
                    new BPMNTestCase().assertTask1()
            ]
    )

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL = builder.buildTaskProcess(
            "SequenceFlowConditional", "A process with three scriptTasks connected by sequenceFlows. " +
            "The first scriptTask points to the other tasks with sequenceFlows. " +
            "One of these sequenceFlows is associated with a conditionExpression",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2(),
                    new BPMNTestCase(2).inputB().assertTask2()
            ]
    )

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT = builder.buildTaskProcess(
            "SequenceFlowConditionalDefault", "A process with three scriptTasks connected by sequenceFlows. " +
            "The first scriptTask points to the other tasks with sequenceFlows. " +
            "One of these sequenceFlows is associated with a conditionExpression, the other one is marked as default",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB().assertTask2()
            ]
    )

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL = builder.buildTaskProcess(
            "SequenceFlowConditionalDefaultNormal", "A process with four scriptTasks connected by sequenceFlows. " +
            "The first scriptTask points to the other three tasks with sequenceFlows. " +
            "The first of these sequenceFlows is associated with a conditionExpression, the second one is marked as default and the third has no condition associated. " +
            "This is a special case document in Sec. 13.2.1, p. 427.",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask3(),
                    new BPMNTestCase(2).inputB().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_SEQUENTIAL = builder.buildTaskProcess(
            "MultiInstanceSequentialTask", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_NONE_BEHAVIOR = builder.buildTaskProcess(
            "MultiInstanceTaskNoneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to none." +
            "The task has a signal boundary event attached that points to another script task. The event should be thrown for every task execution",
            [
                    new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask3().assertTask1().assertTask3().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_ONE_BEHAVIOR = builder.buildTaskProcess(
            "MultiInstanceTaskOneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to one." +
            "The task has a signal boundary event attached that points to another script task. The event should be thrown once.",
            [
                    new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_ALL_BEHAVIOR = builder.buildTaskProcess(
            "MultiInstanceTaskAllBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to all." +
            "The task has a signal boundary event attached that points to another script task. The event should never be thrown.",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_PARALLEL = builder.buildTaskProcess(
            "MultiInstanceParallelTask", "A scriptTask that is marked as a parallel multiInstance task and is enabled three times",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess LOOP_WITH_LOOP_MAXIMUM = builder.buildTaskProcess(
            "LoopTaskWithLoopMaximum", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to true. Additionally a loopMaximum is set to three.",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_FALSE = builder.buildTaskProcess(
            "LoopTaskNoIterationTestBeforeFalse", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false, but has testBefore set to false and, hence, should be executed once.",
            [
                    new BPMNTestCase().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_TRUE = builder.buildTaskProcess(
            "LoopTaskNoIterationTestBeforeTrue", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false and has testBefore set to true. Hence, the task should never be executed.",
            [
                    new BPMNTestCase().assertTask2()
            ]
    )

    public static final List<BPMNProcess> TASKS = [
            SEQUENCE_FLOW,
            SEQUENCE_FLOW_CONDITIONAL,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL,
            MULTI_INSTANCE_SEQUENTIAL,
            MULTI_INSTANCE_NONE_BEHAVIOR,
            MULTI_INSTANCE_ONE_BEHAVIOR,
            MULTI_INSTANCE_ALL_BEHAVIOR,
            MULTI_INSTANCE_PARALLEL,
            LOOP_WITH_LOOP_MAXIMUM,
            LOOP_NO_ITERATION_TEST_BEFORE_FALSE,
            LOOP_NO_ITERATION_TEST_BEFORE_TRUE
    ].flatten() as List<BPMNProcess>
}
