package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.EngineIndependentProcess;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;

import java.util.Arrays;
import java.util.List;

/**
 * Class defining the processes and tests for the basic Workflow Control-Flow Patterns (WCP) 1-20
 */
public class PatternProcesses {

    public static final EngineIndependentProcess WCP01_SEQUENCE = BPMNProcessBuilder.buildPatternProcess(
            "Test Process for WCP01 Sequence: Containing a Start Event, two ScriptTasks (for logging purposes) and an "
                    + "EndEvent. All connected by only basic SequenceFlows."
                    + "Test passed successfully if the trace confirms the execution of 'Task1'.",
            new Feature(new Construct(Groups.CFPATTERNS, "WCP01"), "WCP01_Sequence"),
            new BPMNTestCase().assertTask1()
    );


    public static final EngineIndependentProcess WCP02_PARALLEL_SPLIT = BPMNProcessBuilder.buildPatternProcess(
            "WCP02 ParallelSplit: Checking the ability to create two parallel branches by a ParallelGateway followed by "
                    + "a ScriptTask in each branch."
                    + "Test passed successfully if both Tasks are executed.",
            new Feature(new Construct(Groups.CFPATTERNS, "WCP02"), "WCP02_ParallelSplit"),
            new BPMNTestCase().assertTask1().assertTask2()
    );


    public static final EngineIndependentProcess WCP03_SYNCHRONIZATION = BPMNProcessBuilder.buildPatternProcess(
            "WCP03 Synchronization: Checking the ability to synchronize two parallel branches. The ScriptTask after the "
                    + "merging ParallelGateway has to be executed only once.",
            new Feature(new Construct(Groups.CFPATTERNS, "WCP03"), "WCP03_Synchronization"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );


    public static final EngineIndependentProcess WCP04_EXCLUSIVE_CHOICE = BPMNProcessBuilder.buildPatternProcess(
            "WCP04 Exclusive Choice: Checking the ability to create exclusive branches based on an input. "
                    + "If the input contains 'a' it should execute task1, if it contains 'b' it should execute task2, "
                    + "in any other cases, the default task (task3) should be executed."
                    + "Special case: If the input contains 'a' and 'b' only the first branch must be activated (task1 here)",
            new Feature(new Construct(Groups.CFPATTERNS, "WCP04"), "WCP04_ExclusiveChoice"),
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputAB().assertTask1(),
            new BPMNTestCase().inputB().assertTask2(),
            new BPMNTestCase().inputC().assertTask3()
    );

    public static final EngineIndependentProcess WCP05_SIMPLE_MERGE = BPMNProcessBuilder.buildPatternProcess(
            "WCP05 Simple Merge: Checking the ability to merge multiple branches into a single branch with using a "
                    + "converging XOR gateway. The ScriptTask after the merging gateway must be triggered each time a "
                    + "token arrives.",
            new Feature(new Construct(Groups.CFPATTERNS, "WCP05"), "WCP05_SimpleMerge"),
            new BPMNTestCase().inputA().assertTask1().assertTask4(),
            new BPMNTestCase().inputB().assertTask2().assertTask4(),
            new BPMNTestCase().inputC().assertTask3().assertTask4(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4().assertTask4(),
            new BPMNTestCase().inputAC().assertTask1().assertTask3().assertTask4().assertTask4(),
            new BPMNTestCase().inputBC().assertTask2().assertTask3().assertTask4().assertTask4(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4()
    );

    public static final EngineIndependentProcess WCP06_MULTI_CHOICE_INCLUSIVE_GATEWAY = BPMNProcessBuilder.buildPatternProcess("WCP06_MultiChoice_InclusiveGateway",
            "WCP06 Multi Choice: Checking the ability to perform an OR-Split using an inclusive gateway. One or more branches should be created "
                    + "depending on the input. The third branch is only executed if no other condition is evaluated to true.",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB().assertTask2(),
            new BPMNTestCase().inputC().assertTask3(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2(),
            new BPMNTestCase().inputAC().assertTask1(),
            new BPMNTestCase().inputBC().assertTask2(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess WCP06_MULTI_CHOICE_IMPLICIT = BPMNProcessBuilder.buildPatternProcess("WCP06_MultiChoice_Implicit",
            "WCP06 Multi Choice: Checking the ability to perform an OR-Split using conditional sequence flows without a preceding gateway. " +
                    "One or more branches should be created depending on the input. " +
                    "The third branch is only executed if no other condition is evaluated to true.",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB().assertTask2(),
            new BPMNTestCase().inputC().assertTask3(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2(),
            new BPMNTestCase().inputAC().assertTask1(),
            new BPMNTestCase().inputBC().assertTask2(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess WCP06_MULTI_CHOICE_COMPLEX_GATEWAY = BPMNProcessBuilder.buildPatternProcess("WCP06_MultiChoice_ComplexGateway",
            "WCP06 Multi Choice: Checking the ability to perform an OR-Split using a complex gateway. One or more branches should be created "
                    + "depending on the input. The third branch is only executed if no other condition is evaluated to true.",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB().assertTask2(),
            new BPMNTestCase().inputC().assertTask3(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2(),
            new BPMNTestCase().inputAC().assertTask1(),
            new BPMNTestCase().inputBC().assertTask2(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess WCP07_STRUCTURED_SYNCHRONIZING_MERGE = BPMNProcessBuilder.buildPatternProcess(
            "WCP07_StructuredSynchronizingMerge",
            "WCP07 StructuredSynchronizingMerge: Checks the ability to synchronize the merging of branches created "
                    + "earlier using a multiple choice (see WCP06).",
            new BPMNTestCase().inputA().assertTask1().assertTask3(),
            new BPMNTestCase().inputB().assertTask2().assertTask3(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask3()
    );


    public static final EngineIndependentProcess WCP08_MULTI_MERGE = BPMNProcessBuilder.buildPatternProcess("WCP08_MultiMerge",
            "WCP08 MultiMerge: Tests the convergence of two or more branches into a single path without synchronization."
                    + "The test is equivalent to WCP05 for BPMN.",
            new BPMNTestCase().inputA().assertTask1().assertTask4(),
            new BPMNTestCase().inputB().assertTask2().assertTask4(),
            new BPMNTestCase().inputC().assertTask3().assertTask4(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4().assertTask4(),
            new BPMNTestCase().inputAC().assertTask1().assertTask3().assertTask4().assertTask4(),
            new BPMNTestCase().inputBC().assertTask2().assertTask3().assertTask4().assertTask4(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4()
    );

    public static final EngineIndependentProcess WCP09_STRUCTURED_DISCRIMINATOR_COMPLEXGATEWAY = BPMNProcessBuilder.buildPatternProcess(
            "WCP09_Structured_Discriminator_ComplexGateway",
            "WCP09 Structured Discriminator: Implementation of WCP09 using a merging"
                    + "ComplexGateway with activationCount>=1. I.e, the gateway fires upon completion of the first "
                    + "incoming token and is then disabled.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess WCP09_STRUCTURED_DISCRIMINATOR_MULTI_INSTANCE = BPMNProcessBuilder.buildPatternProcess(
            "WCP09_Structured_Discriminator_MultiInstance",
            "WCP09 partial workaround using MultiInstance: The flow after a MultiInstance Activity should continue after"
                    + "the first instance has completed."
                    + "This covers only a special case for WCP09 Discriminator where one of various EQUAL activities are used.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess WCP_10_ARBITRARY_CYCLES = BPMNProcessBuilder.buildPatternProcess("WCP10_ArbitraryCycles",
            "WCP10 arbitrary cycle: Structure is based on the example on workflowpatterns.com: The test consists of a "
                    + "series of scripttasks and exclusive gateways. After the creation of the log, task1 is logged, "
                    + "afterwards a counter integerVariable is incremented and task1 is repeated if the integerVariable "
                    + "is <2; otherwise task2 is logged which will be repeated if integerVariable<3",
            new BPMNTestCase().setIntegerVariable(2).assertTask1().assertTask2(),
            new BPMNTestCase().setIntegerVariable(1).assertTask1().assertTask2().assertTask2(),
            new BPMNTestCase().setIntegerVariable(0).assertTask1().assertTask1().assertTask2().assertTask2()
    );

    public static final EngineIndependentProcess WCP11_IMPLICIT_TERMINATION = BPMNProcessBuilder.buildPatternProcess("WCP11_ImplicitTermination",
            "A process that terminates when all contained activity instances have completed",
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess WCP12_MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION = BPMNProcessBuilder.buildPatternProcess(
            "WCP12_MultipleInstancesWithoutSynchronization",
            "A process which creates three instances of one script task using multiInstanceLoopCharacteristics, followed by a second activity. " +
                    "The behavior of the multi instance activity is set to None. Hence, a signal should be fired for every complete multi instance activity.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2().assertTask3().assertTask3().assertTask3()
    );

    public static final EngineIndependentProcess WCP13_MULTIPLE_INSTANCES_WITH_A_PRIORI_DESIGN_TIME_KNOWLEDGE = BPMNProcessBuilder.buildPatternProcess(
            "WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge",
            "A process which creates three instances of one script task using multiInstanceLoopCharacteristics, followed by a second activity. " +
                    "The number of instances is hard-coded into the process. The behavior of the multi instance activity is set to 'All'.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess WCP14_MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE = BPMNProcessBuilder.buildPatternProcess(
            "WCP14_MultipleInstancesWithAPrioriRuntimeKnowledge",
            "A process with a multiple instances activity, where the loop cardinality is read from a variable at run-time. " +
                    "The behavior of the multi instance activity is set to 'All'.",
            new BPMNTestCase().setIntegerVariable(3).assertTask1().assertTask1().assertTask1().assertTask2()
    );


    public static final EngineIndependentProcess WCP16_DEFERRED_CHOICE = BPMNProcessBuilder.buildPatternProcess("WCP16_DeferredChoice",
            "An event-based exclusive gateway with two possible branches wait for one out of two signals which are signaled depending on the input. "
                    + "Using a timer, it is ensured that the signals are signaled when the event-based gateway is already waiting for them. "
                    + "Based on EventBasedGateway_Signals",
            new BPMNTestCase().inputA().assertTask1().assertTask3().optionDelay(8000),
            new BPMNTestCase().inputB().assertTask2().assertTask4().optionDelay(8000)
    );

    public static final EngineIndependentProcess WCP17_INTERLEAVED_PARALLEL_ROUTING = BPMNProcessBuilder.buildPatternProcess(
            "WCP17_InterleavedParallelRouting",
            "A set of activity instances is executed sequentially in an " +
                    "order that is decided at run time. No two activity instances of this set are " +
                    "active at the same point in time",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess WCP19_CANCEL_TASK = BPMNProcessBuilder.buildPatternProcess("WCP19_CancelTask",
            "An activity can be canceled when it emits an error event.",
            new BPMNTestCase().assertTask2()
    );

    public static final EngineIndependentProcess WCP20_CANCEL_CASE_ERROR = BPMNProcessBuilder.buildPatternProcess("WCP20_CancelCaseError",
            "Cancels a sub-process by emitting an error event inside the sub-process which is handled through a boundary event. "
                    + "See Error_BoundaryEvent_SubProcess_Interrupting",
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess WCP20_CANCEL_CASE_CANCEL = BPMNProcessBuilder.buildPatternProcess("WCP20_CancelCaseCancel",
            "Cancels a sub-process by emitting a cancel event inside the sub-process which is handled through a boundary event. "
                    + "See Cancel_Event",
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess WCP20_CANCEL_CASE_TERMINATE = BPMNProcessBuilder.buildPatternProcess("WCP20_CancelCaseTerminate",
            "Cancels a process immediatly by emitting a terminate event. See Terminate_Event",
            new BPMNTestCase()
    );

    public static final List<BPMNProcess> PATTERNS = Arrays.asList(
            WCP01_SEQUENCE,

            WCP02_PARALLEL_SPLIT,

            WCP03_SYNCHRONIZATION,

            WCP04_EXCLUSIVE_CHOICE,

            WCP05_SIMPLE_MERGE,

            WCP06_MULTI_CHOICE_INCLUSIVE_GATEWAY,
            WCP06_MULTI_CHOICE_IMPLICIT,
            WCP06_MULTI_CHOICE_COMPLEX_GATEWAY,

            WCP07_STRUCTURED_SYNCHRONIZING_MERGE,

            WCP08_MULTI_MERGE,

            // WCP09 direct solution:
            WCP09_STRUCTURED_DISCRIMINATOR_COMPLEXGATEWAY,
            // WCP09 workaround:
            WCP09_STRUCTURED_DISCRIMINATOR_MULTI_INSTANCE,

            WCP_10_ARBITRARY_CYCLES,

            WCP11_IMPLICIT_TERMINATION,

            WCP12_MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION,

            WCP13_MULTIPLE_INSTANCES_WITH_A_PRIORI_DESIGN_TIME_KNOWLEDGE,

            WCP14_MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE,

            // WCP15 is not supported

            WCP16_DEFERRED_CHOICE,

            WCP17_INTERLEAVED_PARALLEL_ROUTING,

            // WCP18 is not supported

            WCP19_CANCEL_TASK,

            // WCP20 here
            WCP20_CANCEL_CASE_ERROR,
            WCP20_CANCEL_CASE_CANCEL,
            WCP20_CANCEL_CASE_TERMINATE

    );
}
