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

    public static final BPMNProcess MULTI_INSTANCE_SUBPROCESS = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_SubProcess", "A subProcess that defines multiInstanceLoopCharactestics with"
                    + " sequential behavior should be executed three times. Within the subProcess 'task1' is logged.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_TASK = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_Task", "A scriptTask that is marked as a multiInstance task and is enabled three times",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_SEQUENTIAL = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_Sequential", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_NONE_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_NoneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to none." +
                    "The task has a signal boundary event attached that points to another script task. The event should be thrown for every task execution",
            new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask3().assertTask1().assertTask3().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_ONE_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_OneBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to one." +
                    "The task has a signal boundary event attached that points to another script task. The event should be thrown once.",
            new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_ALL_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_AllBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to all." +
                    "The task has a signal boundary event attached that points to another script task. The event should never be thrown.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTI_INSTANCE_COMPLEX_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_ComplexBehavior", "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to 'complex'." +
                    "The task has a signal boundary event attached that points to another script task which is triggered upon completion of the first instance."
                    + "All remaining instances are canceled.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final BPMNProcess MULTI_INSTANCE_PARALLEL = BPMNProcessBuilder.buildActivityProcess(
            "MultiInstance_Parallel", "A scriptTask that is marked as a parallel multiInstance task and is enabled three times",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess LOOP_SUBPROCESS = BPMNProcessBuilder.buildActivityProcess("Loop_SubProcess",
            "A subProcess with standardLoopCharacteristics which should be looped as long as 'integerVariable' is less than 3."
                    + "Each time the subProcess is executed a scripttask logs 'INCREMENT'."
                    + "The default for the attribute 'testBefore' is used, which is 'false', i.e., the loopCondition is "
                    + "evaluated after the execution (do-while semantics)."
                    + "After the looped task 'task2' is executed once.",
            new BPMNTestCase().setIntegerVariable(3).assertIncrement().assertTask1(),
            new BPMNTestCase().setIntegerVariable(1).assertIncrement().assertIncrement().assertTask1(),
            new BPMNTestCase().setIntegerVariable(0).assertIncrement().assertIncrement().assertIncrement().assertTask1()
    );

    public static final BPMNProcess LOOP_TASK = BPMNProcessBuilder.buildActivityProcess("Loop_Task",
            "A scriptTask with standardLoopCharacteristics which should be looped as long as 'integerVariable' is less than 3."
                    + "Each time the task is executed 'INCREMENT' is logged. "
                    + "The default for the attribute 'testBefore' is used, which is 'false', i.e., the loopCondition is "
                    + "evaluated after the execution (do-while semantics)."
                    + "After the looped task 'task2' is executed once.",
            new BPMNTestCase().setIntegerVariable(3).assertIncrement().assertTask2(),
            new BPMNTestCase().setIntegerVariable(1).assertIncrement().assertIncrement().assertTask2(),
            new BPMNTestCase().setIntegerVariable(0).assertIncrement().assertIncrement().assertIncrement().assertTask2()
    );

    public static final BPMNProcess LOOP_CONDITION_ONLY = BPMNProcessBuilder.buildActivityProcess("Loop_ConditionOnly",
            "A scriptTask with standardLoopCharacteristics which should be looped as long as 'integerVariable' is less than 3."
                    + "Each time the task is executed 'INCREMENT' is logged. "
                    + "The default for the attribute 'testBefore' is used, which is 'false', i.e., the loopCondition is "
                    + "evaluated after the execution (do-while semantics)."
                    + "After the looped task 'task2' is executed once.",
            new BPMNTestCase().setIntegerVariable(3).assertIncrement().assertTask2(),
            new BPMNTestCase().setIntegerVariable(1).assertIncrement().assertIncrement().assertTask2(),
            new BPMNTestCase().setIntegerVariable(0).assertIncrement().assertIncrement().assertIncrement().assertTask2()
    );


    public static final BPMNProcess LOOP_MAXIMUM = BPMNProcessBuilder.buildActivityProcess(
            "Loop_Maximum", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to true. Additionally a loopMaximum is set to three.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_FALSE = BPMNProcessBuilder.buildActivityProcess(
            "Loop_NoIteration_TestBeforeFalse", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false, but has testBefore set to false and, hence, should be executed once.",
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final BPMNProcess LOOP_NO_ITERATION_TEST_BEFORE_TRUE = BPMNProcessBuilder.buildActivityProcess(
            "Loop_NoIteration_TestBeforeTrue", "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false and has testBefore set to true. Hence, the task should never be executed.",
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

    public static final BPMNProcess SEND_TASK = BPMNProcessBuilder.buildActivityProcess(
            "SendTask", "A test with two processes, which start in parallel. " +
                    "The first one sends a message to the second one via a send task." +
                    "The message is caught in an intermediate event.",
            new BPMNTestCase().useParallelProcess().assertTask1().assertMarkerExists()
    );


    public static final List<BPMNProcess> ACTIVITIES = Arrays.asList(

            CALL_ACTIVITY_PROCESS,
            CALL_ACTIVITY_GLOBAL_TASK,

            MULTI_INSTANCE_SUBPROCESS,
            MULTI_INSTANCE_TASK,
            MULTI_INSTANCE_SEQUENTIAL,
            MULTI_INSTANCE_NONE_BEHAVIOR,
            MULTI_INSTANCE_ONE_BEHAVIOR,
            MULTI_INSTANCE_ALL_BEHAVIOR,
            MULTI_INSTANCE_COMPLEX_BEHAVIOR,
            MULTI_INSTANCE_PARALLEL,

            LOOP_SUBPROCESS,
            LOOP_TASK,
            LOOP_CONDITION_ONLY,
            LOOP_MAXIMUM,
            LOOP_NO_ITERATION_TEST_BEFORE_FALSE,
            LOOP_NO_ITERATION_TEST_BEFORE_TRUE,

            SUB_PROCESS,

            TRANSACTION,

            AD_HOC_SUB_PROCESS_SEQUENTIAL,
            AD_HOC_SUB_PROCESS_PARALLEL,

            TOKEN_CARDINALITY_EXPLICIT,
            TOKEN_CARDINALITY_DEFAULT,
            TOKEN_CARDINALITY_SPLIT_DEFAULT,
            TOKEN_CARDINALITY_SPLIT_EXPLICIT,

            SEND_TASK
    );
}
