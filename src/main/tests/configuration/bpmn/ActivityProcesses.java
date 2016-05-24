package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;

import java.util.Arrays;
import java.util.List;

class ActivityProcesses {

    /*
    * ACTIVITY CONSTRUCTS
    */

    public static final Construct CONSTRUCT_CALL_ACTIVITY = new Construct(Groups.ACTIVITIES, "CallActivity",
            "A Call Activity identifies a point in the Process where a global Process or a Global Task is used. The Call "
                    + "Activity acts as a ‘wrapper’ for the invocation of a global Process or Global Task within the "
                    + "execution. (see BPMN spec p.182)");
    public static final Construct CONSTRUCT_MULTI_INSTANCE_TASK = new Construct(Groups.ACTIVITIES, "MultiInstanceTask",
            "MultiInstanceTasks allows for creation of a desired number of Activity instances. The instances can be "
                    + "defined to be executed in parallel or sequential. (see BPMN spec p.190)");

    public static final Construct CONSTRUCT_LOOP_TASK = new Construct(Groups.ACTIVITIES, "LoopTask", "The StandardLoopCharacteristics class defines "
            + "looping behavior based on a boolean condition. The Activity will loop as long as the boolean "
            + "condition is true. (see BPMN spec p.189)");

    public static final Construct CONSTRUCT_TRANSACTION = new Construct(Groups.ACTIVITIES, "Transaction", "A Transaction is a specialized type of "
            + "Sub-Process that will have a special behavior that is controlled through a transaction protocol."
            + " (see BPMN spec p.176)");

    public static final Construct CONSTRUCT_SUBPROCESS = new Construct(Groups.ACTIVITIES, "SubProcess", "A Sub-Process is an Activity whose internal "
            + "details have been modeled using Activities, Gateways, Events, and Sequence Flows. (see BPMN spec p.171)");

    public static final Construct CONSTRUCT_AD_HOC_SUBPROCESS = new Construct(Groups.ACTIVITIES, "AdHocSubProcess", "An Ad-Hoc Sub-Process is a specialized type"
            + " of Sub-Process that is a group of Activities that have no required sequence relationships. (see BPMN spec p.179)");

    public static final Construct CONSTRUCT_TOKEN_CARDINALITY = new Construct(Groups.ACTIVITIES, "TokenCardinality",
            "All activities can specify how much so called tokens are needed to start the execution/are created upon "
                    + "completion. (see BPMN spec pp.150-151)");

    public static final Construct CONSTRUCT_SEND_TASK = new Construct(Groups.ACTIVITIES, "SendTask", "A Send Task is a "
            + "simple Task that is designed to send a Message to an external Participant (relative to the Process). "
            + "Once the Message has been sent, the Task is completed. (see BPMN spec p.157)");

    public static final Construct CONSTRUCT_RECEIVE_TASK = new Construct(Groups.ACTIVITIES, "ReceiveTask", "A Receive "
            + "Task is a simple Task that is designed to wait for a Message to arrive from an external Participant "
            + "(relative to the Process). Once the Message has been received, the Task is completed. (see BPMN spec p.159)");


    /*
     * FEATURE TESTS:
     */

    public static final EngineIndependentProcess CALL_ACTIVITY_PROCESS = BPMNProcessBuilder.buildActivityProcess("CallActivity_Process",
            "A collaboration with two participants. One process calls the other one through a "
                    + "callActivity. The second process creates a file MARKER which must be present to pass the test.",
            new Feature(CONSTRUCT_CALL_ACTIVITY, "CallActivity_Process"),
            new BPMNTestCase().assertMarkerExists().assertTask2());

    public static final EngineIndependentProcess CALL_ACTIVITY_GLOBAL_TASK = BPMNProcessBuilder.buildActivityProcess("CallActivity_GlobalTask",
            "Definitions contains a GlobalScriptTask which is called by a CallActivity.",
            new Feature(CONSTRUCT_CALL_ACTIVITY, "CallActivity_GlobalTask"),
            new BPMNTestCase().assertTask1().assertTask2());
    public static final EngineIndependentProcess MULTI_INSTANCE_SUBPROCESS = BPMNProcessBuilder.buildActivityProcess("MultiInstance_SubProcess",
            "A subProcess that defines multiInstanceLoopCharactestics with"
                    + " sequential behavior should be executed three times. Within the subProcess 'task1' is logged.",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_SubProcess"),
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final EngineIndependentProcess MULTI_INSTANCE_TASK = BPMNProcessBuilder.buildActivityProcess("MultiInstance_Task",
            "A scriptTask that is marked as a multiInstance task and is enabled three times",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_Task"),
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final EngineIndependentProcess MULTI_INSTANCE_SEQUENTIAL = BPMNProcessBuilder.buildActivityProcess("MultiInstance_Sequential",
            "A scriptTask that is marked as a sequential multiInstance task and is enabled three times",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_Sequential"),
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final EngineIndependentProcess MULTI_INSTANCE_NONE_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess("MultiInstance_NoneBehavior",
            "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to 'none'. " +
                    "The task has a signal boundary event attached that points to another script task. The event should be thrown for every task execution",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_NoneBehavior"),
            new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask3().assertTask1().assertTask3().assertTask2());

    public static final EngineIndependentProcess MULTI_INSTANCE_ONE_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess("MultiInstance_OneBehavior",
            "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to 'one'. " +
                    "The task has a signal boundary event attached that points to another script task. The event should be thrown once.",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_OneBehavior"),
            new BPMNTestCase().assertTask1().assertTask3().assertTask1().assertTask1().assertTask2());

    public static final EngineIndependentProcess MULTI_INSTANCE_ALL_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess("MultiInstance_AllBehavior",
            "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to 'all'. " +
                    "The task has a signal boundary event attached that points to another script task. The event should never be thrown.",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_AllBehavior"),
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final EngineIndependentProcess MULTI_INSTANCE_COMPLEX_BEHAVIOR = BPMNProcessBuilder.buildActivityProcess("MultiInstance_ComplexBehavior",
            "A scriptTask that is marked as a sequential multiInstance task and is enabled three times and its behavior set to 'complex'. " +
                    "The task has a signal boundary event attached that points to another script task which is triggered upon completion of the first instance. "
                    + "All remaining instances are canceled.",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_ComplexBehavior"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());

    public static final EngineIndependentProcess MULTI_INSTANCE_PARALLEL = BPMNProcessBuilder.buildActivityProcess("MultiInstance_Parallel",
            "A scriptTask that is marked as a parallel multiInstance task and is enabled three times.",
            new Feature(CONSTRUCT_MULTI_INSTANCE_TASK, "MultiInstance_Parallel"),
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final EngineIndependentProcess LOOP_SUBPROCESS = BPMNProcessBuilder.buildActivityProcess("Loop_SubProcess",
            "A subProcess with standardLoopCharacteristics which should be looped as long as 'integerVariable' is less than 3. "
                    + "Each time the subProcess is executed a scripttask logs 'INCREMENT'. "
                    + "The default for the attribute 'testBefore' is used, which is 'false', i.e., the loopCondition is "
                    + "evaluated after the execution (do-while semantics). "
                    + "After the looped task 'task2' is executed once.",
            new Feature(CONSTRUCT_LOOP_TASK, "Loop_SubProcess"),
            new BPMNTestCase().setIntegerVariable(3).assertIncrement().assertTask1(), new BPMNTestCase().setIntegerVariable(1).assertIncrement().assertIncrement().assertTask1(), new BPMNTestCase().setIntegerVariable(0).assertIncrement().assertIncrement().assertIncrement().assertTask1());

    public static final EngineIndependentProcess LOOP_TASK = BPMNProcessBuilder.buildActivityProcess("Loop_Task",
            "A scriptTask with standardLoopCharacteristics which should be looped as long as 'integerVariable' is less than 3."
                    + "Each time the task is executed 'INCREMENT' is logged. "
                    + "The default for the attribute 'testBefore' is used, which is 'false', i.e., the loopCondition is "
                    + "evaluated after the execution (do-while semantics). "
                    + "After the looped task 'task2' is executed once.",
            new Feature(CONSTRUCT_LOOP_TASK, "Loop_Task"),
            new BPMNTestCase().setIntegerVariable(3).assertIncrement().assertTask2(), new BPMNTestCase().setIntegerVariable(1).assertIncrement().assertIncrement().assertTask2(), new BPMNTestCase().setIntegerVariable(0).assertIncrement().assertIncrement().assertIncrement().assertTask2());

    public static final EngineIndependentProcess LOOP_CONDITION_ONLY = BPMNProcessBuilder.buildActivityProcess("Loop_ConditionOnly",
            "A scriptTask with standardLoopCharacteristics which should be looped as long as 'integerVariable' is less than 3. "
                    + "Each time the task is executed 'INCREMENT' is logged. "
                    + "The default for the attribute 'testBefore' is used, which is 'false', i.e., the loopCondition is "
                    + "evaluated after the execution (do-while semantics). "
                    + "After the looped task 'task2' is executed once.",
            new Feature(CONSTRUCT_LOOP_TASK, "Loop_ConditionOnly"),
            new BPMNTestCase().setIntegerVariable(3).assertIncrement().assertTask2(), new BPMNTestCase().setIntegerVariable(1).assertIncrement().assertIncrement().assertTask2(), new BPMNTestCase().setIntegerVariable(0).assertIncrement().assertIncrement().assertIncrement().assertTask2());


    public static final EngineIndependentProcess LOOP_MAXIMUM = BPMNProcessBuilder.buildActivityProcess("Loop_Maximum",
            "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to true. Additionally a loopMaximum is set to three.",
            new Feature(CONSTRUCT_LOOP_TASK, "Loop_Maximum"),
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final EngineIndependentProcess LOOP_NO_ITERATION_TEST_BEFORE_FALSE = BPMNProcessBuilder.buildActivityProcess("Loop_NoIteration_TestBeforeFalse",
            "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false, but has testBefore set to false and, hence, should be executed once.",
            new Feature(CONSTRUCT_LOOP_TASK, "Loop_NoIteration_TestBeforeFalse"),
            new BPMNTestCase().assertTask1().assertTask2());

    public static final EngineIndependentProcess LOOP_NO_ITERATION_TEST_BEFORE_TRUE = BPMNProcessBuilder.buildActivityProcess("Loop_NoIteration_TestBeforeTrue",
            "A scriptTask with standardLoopCharacteristics and a condition that always evaluates to false and has testBefore set to true. Hence, the task should never be executed.",
            new Feature(CONSTRUCT_LOOP_TASK, "Loop_NoIteration_TestBeforeTrue"),
            new BPMNTestCase().assertTask2());

    public static final EngineIndependentProcess SUB_PROCESS = BPMNProcessBuilder.buildActivityProcess("SubProcess",
            "A process that contains a subProcess",
            new Feature(CONSTRUCT_SUBPROCESS, "SubProcess"),
            new BPMNTestCase().assertTask1().assertTask2());

    public static final EngineIndependentProcess TRANSACTION = BPMNProcessBuilder.buildActivityProcess("Transaction",
            "A process that contains a transaction",
            new Feature(CONSTRUCT_TRANSACTION, "Transaction"),
            new BPMNTestCase().assertTask1().assertTask2());

    public static final EngineIndependentProcess AD_HOC_SUB_PROCESS_SEQUENTIAL = BPMNProcessBuilder.buildActivityProcess("AdHocSubProcess_Sequential",
            "A process that contains an adHocSubProcess, which executes two contained tasks sequentially",
            new Feature(CONSTRUCT_AD_HOC_SUBPROCESS, "AdHocSubProcess_Sequential"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());

    public static final EngineIndependentProcess AD_HOC_SUB_PROCESS_PARALLEL = BPMNProcessBuilder.buildActivityProcess("AdHocSubProcess_Parallel",
            "A process that contains an adHocSubProcess, which executes two contained tasks in parallel",
            new Feature(CONSTRUCT_AD_HOC_SUBPROCESS, "AdHocSubProcess_Parallel"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());

    public static final EngineIndependentProcess TOKEN_CARDINALITY_EXPLICIT = BPMNProcessBuilder.buildActivityProcess("Token_Cardinality_Explicit",
            "A process that contains one scriptTask with completionQuantity=2, one scriptTask with startQuantity=2, " +
                    "and one scriptTask in between with both set to 1. The scriptTask in between must therefore be executed twice.",
            new Feature(CONSTRUCT_TOKEN_CARDINALITY, "Token_Cardinality_Explicit"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask2().assertTask3());

    public static final EngineIndependentProcess TOKEN_CARDINALITY_DEFAULT = BPMNProcessBuilder.buildActivityProcess("Token_Cardinality_Default",
            "A process that builds upon TOKEN_CARDINALITY_EXPLICIT, only deferring in leaving the startQuantity and completionQuantity attributes " +
                    "of the middle scriptTasks at their default values, i.e. 1 for both of them. Thus, this process must behave exactly like TOKEN_CARDINALITY_EXPLICIT.",
            new Feature(CONSTRUCT_TOKEN_CARDINALITY, "Token_Cardinality_Default"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask2().assertTask3());

    public static final EngineIndependentProcess TOKEN_CARDINALITY_SPLIT_DEFAULT = BPMNProcessBuilder.buildActivityProcess("Token_Cardinality_Split_Default",
            "A process that contains one scriptTask which has two outgoing sequence flows, each leading to one of two forwarding scriptTasks. " +
                    "It is expected that each forward task is executed once and therefore the last and receiving scriptTask is executed twice.",
            new Feature(CONSTRUCT_TOKEN_CARDINALITY, "Token_Cardinality_Split_Default"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4());

    public static final EngineIndependentProcess TOKEN_CARDINALITY_SPLIT_EXPLICIT = BPMNProcessBuilder.buildActivityProcess("Token_Cardinality_Split_Explicit",
            "A process that contains one scriptTask which has two outgoing sequence flows, each leading to one of two forwarding scriptTasks. " +
                    "It is expected that each forward task is executed once and therefore the last and receiving scriptTask is executed once because of startingQuantity.",
            new Feature(CONSTRUCT_TOKEN_CARDINALITY, "Token_Cardinality_Split_Explicit"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4());

    public static final EngineIndependentProcess SEND_TASK = BPMNProcessBuilder.buildActivityProcess("SendTask",
            "A test with two processes, which start in parallel. " +
                    "The first one sends a message to the second one via a send task. " +
                    "The message is caught in an intermediate event.",
            new Feature(CONSTRUCT_SEND_TASK, "SendTask"),
            new BPMNTestCase().useParallelProcess().assertTask1().assertMarkerExists());

    public static final EngineIndependentProcess RECEIVE_TASK = BPMNProcessBuilder.buildActivityProcess("ReceiveTask",
            "A test with two processes, which start in parallel. " +
                    "The first one sends a message to the second one via an intermediate message event. " +
                    "The message is caught in a receive task.",
            new Feature(CONSTRUCT_RECEIVE_TASK, "ReceiveTask"),
            new BPMNTestCase().useParallelProcess().assertTask1().assertMarkerExists());

    public static final EngineIndependentProcess RECEIVE_TASK_INSTANTIATE = BPMNProcessBuilder.buildActivityProcess("ReceiveTask_Instantiate",
            "A test with two processes. " +
                    "The first one sends a message to the second one via an intermediate message event. " +
                    "The message is caught in a receive task, which instantiates the second process",
            new Feature(CONSTRUCT_RECEIVE_TASK, "ReceiveTask_Instantiate"),
            new BPMNTestCase().assertTask1().assertMarkerExists());


    public static final List<EngineIndependentProcess> ACTIVITIES = Arrays.asList(

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

            SEND_TASK,

            RECEIVE_TASK,
            RECEIVE_TASK_INSTANTIATE
    );
}
