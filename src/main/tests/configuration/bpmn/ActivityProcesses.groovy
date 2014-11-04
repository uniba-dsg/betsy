package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class ActivityProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess CALL_ACTIVITY_PROCESS = builder.buildActivityProcess(
            "CallActivity_Process", "A collaboration with two participants. One process calls the other one through a callActivity.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess CALL_ACTIVITY_GLOBAL_TASK = builder.buildActivityProcess(
            "CallActivity_GlobalTask", "Definitions contains a GlobalScriptTask which is called by a CallActivity.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_TASK_SEQUENTIAL = builder.buildActivityProcess(
            "MultiInstanceTask_Sequential", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_TASK_NONE_BEHAVIOR = builder.buildActivityProcess(
            "MultiInstanceTask_NoneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to none." +
            "The task has a signal boundary event attached that points to another script task. The event should be thrown for every task execution",
            [
                    new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask3().assertTask1().assertTask3().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_TASK_ONE_BEHAVIOR = builder.buildActivityProcess(
            "MultiInstanceTask_OneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to one." +
            "The task has a signal boundary event attached that points to another script task. The event should be thrown once.",
            [
                    new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_TASK_ALL_BEHAVIOR = builder.buildActivityProcess(
            "MultiInstanceTask_AllBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to all." +
            "The task has a signal boundary event attached that points to another script task. The event should never be thrown.",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess MULTI_INSTANCE_TASK_PARALLEL = builder.buildActivityProcess(
            "MultiInstanceTask_Parallel", "A scriptTask that is marked as a parallel multiInstance task and is enabled three times",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess LOOP_TASK_LOOP_MAXIMUM = builder.buildActivityProcess(
            "LoopTask_LoopMaximum", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to true. Additionally a loopMaximum is set to three.",
            [
                    new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_FALSE = builder.buildActivityProcess(
            "LoopTask_NoIteration_TestBeforeFalse", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false, but has testBefore set to false and, hence, should be executed once.",
            [
                    new BPMNTestCase().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_TRUE = builder.buildActivityProcess(
            "LoopTask_NoIteration_TestBeforeTrue", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false and has testBefore set to true. Hence, the task should never be executed.",
            [
                    new BPMNTestCase().assertTask2()
            ]
    )

    public static final BPMNProcess SUB_PROCESS = builder.buildActivityProcess(
            "SubProcess", "A process that contains a subProcess",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess TRANSACTION = builder.buildActivityProcess(
            "Transaction", "A process that contains a transaction",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final List<BPMNProcess> ACTIVITIES = [

            CALL_ACTIVITY_PROCESS,
            CALL_ACTIVITY_GLOBAL_TASK,

            MULTI_INSTANCE_TASK_SEQUENTIAL,
            MULTI_INSTANCE_TASK_NONE_BEHAVIOR,
            MULTI_INSTANCE_TASK_ONE_BEHAVIOR,
            MULTI_INSTANCE_TASK_ALL_BEHAVIOR,
            MULTI_INSTANCE_TASK_PARALLEL,

            LOOP_TASK_LOOP_MAXIMUM,
            LOOP_NO_ITERATION_TEST_BEFORE_FALSE,
            LOOP_NO_ITERATION_TEST_BEFORE_TRUE,

            SUB_PROCESS,

            TRANSACTION
    ].flatten() as List<BPMNProcess>
}
