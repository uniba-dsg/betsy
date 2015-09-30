package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

import java.util.Arrays;
import java.util.List;

class ActivityProcesses {

    public static final BPMNProcess CALL_ACTIVITY_PROCESS = BPMNProcessBuilder.buildActivityProcess(
            "CallActivity_Process", "A collaboration with two participants. One process calls the other one through a "
                    + "callActivity. The second process creates a file MARKER which must be present to pass the test.",
            new BPMNTestCase().assertMarkerExists().assertTask2()
    );

    public static final BPMNProcess CALL_ACTIVITY_GLOBAL_TASK = BPMNProcessBuilder.buildActivityProcess(
            "CallActivity_GlobalTask", "Definitions contains a GlobalScriptTask which is called by a CallActivity.",
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_TASK_SEQUENTIAL = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstanceTask_Sequential", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_TASK_NONE_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstanceTask_NoneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to none." +
                    "The task has a signal boundary event attached that points to another script task. The event should be thrown for every task execution",
            new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask3().assertTask1().assertTask3().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_TASK_ONE_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstanceTask_OneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to one." +
                    "The task has a signal boundary event attached that points to another script task. The event should be thrown once.",
            new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_TASK_ALL_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstanceTask_AllBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to all." +
                    "The task has a signal boundary event attached that points to another script task. The event should never be thrown.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_TASK_PARALLEL = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstanceTask_Parallel", "A scriptTask that is marked as a parallel multiInstance task and is enabled three times",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess LOOP_TASK_LOOP_MAXIMUM = BPMNProcessBuilder.buildActivityProcess(
            "LoopTask_LoopMaximum", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to true. Additionally a loopMaximum is set to three.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_FALSE = BPMNProcessBuilder.buildActivityProcess(
            "LoopTask_NoIteration_TestBeforeFalse", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false, but has testBefore set to false and, hence, should be executed once.",
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_TRUE = BPMNProcessBuilder.buildActivityProcess(
            "LoopTask_NoIteration_TestBeforeTrue", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false and has testBefore set to true. Hence, the task should never be executed.",
            new BPMNTestCase().assertTask2()
    );

    public static final BPMNProcess SUB_PROCESS = BPMNProcessBuilder.buildActivityProcess(
            "SubProcess", "A process that contains a subProcess",
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final BPMNProcess TRANSACTION = BPMNProcessBuilder.buildActivityProcess(
            "Transaction", "A process that contains a transaction",
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final BPMNProcess AD_HOC_SUB_PROCESS_SEQUENTIAL = BPMNProcessBuilder.buildActivityProcess(
            "AdHocSubProcess_Sequential",
            "A process that contains an adHocSubProcess, which executes two contained tasks sequentially",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final BPMNProcess AD_HOC_SUB_PROCESS_PARALLEL = BPMNProcessBuilder.buildActivityProcess(
            "AdHocSubProcess_Sequential",
            "A process that contains an adHocSubProcess, which executes two contained tasks sequentially",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final BPMNProcess TOKEN_CARDINALITY_EXPLICIT = BPMNProcessBuilder.buildActivityProcess(
            "Token_Cardinality_Explicit", "A process that contains one scriptTask with completionQuantity=2, one scriptTask with startQuantity=2, " +
                    "and one scriptTask in between with both set to 1. The scriptTask in between must therefore be executed twice.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask2().assertTask3()
    );

    public static final BPMNProcess TOKEN_CARDINALITY_DEFAULT = BPMNProcessBuilder.buildActivityProcess(
            "Token_Cardinality_Default", "A process that builds upon TOKEN_CARDINALITY_EXPLICIT, only deferring in leaving the startQuantity and completionQuantity attributes" +
                    "of the middle scriptTasks at their default values, i.e. 1 for both of them. Thus, this process must behave exactly like TOKEN_CARDINALITY_EXPLICIT.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask2().assertTask3()
    );

    public static final BPMNProcess TOKEN_CARDINALITY_SPLIT_DEFAULT = BPMNProcessBuilder.buildActivityProcess(
            "Token_Cardinality_Split_Default", "A process that contains one scriptTask which has two outgoing sequence flows, each leading to one of two forwarding scriptTasks." +
                    "It is expected that each forward task is executed once and therefore the last and receiving scriptTask is executed twice.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4()
    );

    public static final BPMNProcess TOKEN_CARDINALITY_SPLIT_EXPLICIT = BPMNProcessBuilder.buildActivityProcess(
            "Token_Cardinality_Split_Explicit", "A process that contains one scriptTask which has two outgoing sequence flows, each leading to one of two forwarding scriptTasks." +
                    "It is expected that each forward task is executed once and therefore the last and receiving scriptTask is executed once because of startingQuantity.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4()
    );


    public static final List<BPMNProcess> ACTIVITIES = Arrays.asList(

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

            TRANSACTION,

            AD_HOC_SUB_PROCESS_SEQUENTIAL,
            AD_HOC_SUB_PROCESS_PARALLEL,

            TOKEN_CARDINALITY_EXPLICIT,
            TOKEN_CARDINALITY_DEFAULT,
            TOKEN_CARDINALITY_SPLIT_DEFAULT,
            TOKEN_CARDINALITY_SPLIT_EXPLICIT
    );
}
