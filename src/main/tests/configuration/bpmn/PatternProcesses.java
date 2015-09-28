package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Class defining the processes and tests for the basic Workflow Control-Flow Patterns (WCP) 1-20
 */
public class PatternProcesses {

    public static final BPMNProcess WCP01_SEQUENCE = BPMNProcessBuilder.buildPatternProcess("WCP01_Sequence",
            "Test Process for WCP01 Sequence: Containing a Start Event, two ScriptTasks (for logging purposes) and an "
                    + "EndEvent. All connected by only basic SequenceFlows."
                    + "Test passed successfully if the trace confirms the execution of 'Task1'.",
            new BPMNTestCase().assertTask1());


    public static final BPMNProcess WCP02_PARALLEL_SPLIT = BPMNProcessBuilder.buildPatternProcess("WCP02_ParallelSplit",
            "WCP02 ParallelSplit: Checking the ability to create two parallel branches by a ParallelGateway followed by "
                    + "a ScriptTask in each branch."
                    + "Test passed successfully if both Tasks are executed.",
            new BPMNTestCase().assertTask1().assertTask2());


    public static final BPMNProcess WCP03_SYNCHRONIZATION = BPMNProcessBuilder.buildPatternProcess("WCP03_Synchronization",
            "WCP03 Synchronization: Checking the ability to synchronize two parallel branches. The ScriptTask after the "
                    + "merging ParallelGateway has to be executed only once.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());


    public static final BPMNProcess WCP04_EXCLUSIVE_CHOICE = BPMNProcessBuilder.buildPatternProcess("WCP04_ExclusiveChoice",
            "WCP04 Exclusive Choice: Checking the ability to create exclusive branches based on an input. "
                    + "If the input contains 'a' it should execute task1, if it contains 'b' it should execute task2, "
                    + "in any other cases, the default task (task3) should be executed."
                    + "Special case: If the input contains 'a' and 'b' only the first branch must be activated (task1 here)",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputAB().assertTask1(),
            new BPMNTestCase().inputB().assertTask2(),
            new BPMNTestCase().inputC().assertTask3()
    );

    public static final BPMNProcess WCP05_SIMPLE_MERGE = BPMNProcessBuilder.buildPatternProcess("WCP05_SimpleMerge",
            "WCP05 Simple Merge: Checking the ability to merge multiple branches into a single branch with using a "
                    + "converging XOR gateway. The ScriptTask after the merging gateway must be triggered each time a "
                    + "token arrives.",
            new BPMNTestCase().inputA().assertTask1().assertTask4(),
            new BPMNTestCase().inputB().assertTask2().assertTask4(),
            new BPMNTestCase().inputC().assertTask3().assertTask4(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4().assertTask4(),
            new BPMNTestCase().inputAC().assertTask1().assertTask3().assertTask4().assertTask4(),
            new BPMNTestCase().inputBC().assertTask2().assertTask3().assertTask4().assertTask4(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4()
    );

    public static final BPMNProcess WCP06_MULTI_CHOICE = BPMNProcessBuilder.buildPatternProcess("WCP06_MultiChoice",
            "WCP06 Multi Choice: Checking the ability to perform an OR-Split, one or more branches should be created "
                    + "depending on the input. Task3 is only executed if no other condition is evaluated to true.",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB().assertTask2(),
            new BPMNTestCase().inputC().assertTask3(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2(),
            new BPMNTestCase().inputAC().assertTask1(),
            new BPMNTestCase().inputBC().assertTask2(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2()
    );


    public static final BPMNProcess WCP07_STRUCTURED_SYNCHRONIZING_MERGE = BPMNProcessBuilder.buildPatternProcess(
            "WCP07_StructuredSynchronizingMerge",
            "WCP07 StructuredSynchronizingMerge: Checks the ability to synchronize the merging of branches created "
                    + "earlier using a multiple choice (see WCP06).",
            new BPMNTestCase().inputA().assertTask1().assertTask3(),
            new BPMNTestCase().inputB().assertTask2().assertTask3(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask3()
    );


    public static final BPMNProcess WCP08_MULTI_MERGE = BPMNProcessBuilder.buildPatternProcess("WCP08_MultiMerge",
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


    public static final BPMNProcess DISCRIMINATOR_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP09Discriminator", "A point in the workflow process that waits for one of the" +
                    " incoming branches to complete before activating the subsequent activity\n" + "this is achieved by an N out of M MultiInstance join that completes after ONE instance as depicted in Wohed2005",
            new BPMNTestCase().assertTask1().assertTask2());

    public static final BPMNProcess ARBITRARY_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP10ArbitraryCycleMM", "An arbitrary Cycle realized by the MultiMerge Solution depicted in Weske2012 Fig.4.18\n" +
                    "This solution might not work in JBPM because of the implementation used for the Multimerge",

            new BPMNTestCase().assertTask2().assertTask4().assertTask4().assertTask5().assertTask5().assertTask5().assertTask5().assertTask5());


    public static final BPMNProcess ARBITRARY_PATTERN_2 = BPMNProcessBuilder.buildPatternProcess("WCP10ArbitraryCycle", "An arbitrary Cycle realized without the MultiMerge Solution depicted in Weske2012 Fig.4.17\n" +
                    "This solution should work with JBPM since it uses an alternative for Multimerge",

            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final BPMNProcess IMPLICIT_TERMINATION = BPMNProcessBuilder.buildPatternProcess("WCP11_ImplicitTermination",
            "A process that terminates when all contained activity instances have completed",
            new BPMNTestCase().assertTask1()
    );

    public static final BPMNProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION = BPMNProcessBuilder.buildPatternProcess("WCP12_MultipleInstancesWithoutSynchronization",
            "A process which creates three instances of one script task using multiInstanceLoopCharacteristics, followed by a second activity",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_DESIGN_TIME_KNOWLEDGE = BPMNProcessBuilder.buildPatternProcess("WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge",
            "A process which creates three instances of one script task using multiInstanceLoopCharacteristics, followed by a second activity. " +
                    "The number of instances is hard-coded into the process.",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2()
    );

    public static final BPMNProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE = BPMNProcessBuilder.buildPatternProcess("WCP14_MultipleInstancesWithAPrioriRuntimeKnowledge",
            "A process with a multiple instances activity, where the loop cardinality is read from a variable at run-time",
            new BPMNTestCase().setIntegerVariable(3).assertTask1().assertTask1().assertTask1().assertTask2());


    public static final BPMNProcess DEFERRED_CHOICE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP16DeferredChoice", "A process with the ability to depict a divergence point in a process where\n" +
                    "one of several possible branches should be activated.",
            new BPMNTestCase().assertTask1().optionDelay(5000));


    //Note: doesn't work with jbpm due to MI usage
    public static final BPMNProcess INTER_PAR_ROUTING_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP17InterleavedParallelRouting", " A set of activity instances is executed sequentially in an\n" +
                    "order that is decided at run time. No two activity instances of this set are\n" +
                    "active at the same point in time",
            new BPMNTestCase().assertTask1().assertTask2().assertTask2().assertTask2());

    public static final BPMNProcess INTER_PAR_ROUTING_PATTERN_AD_HOC = BPMNProcessBuilder.buildPatternProcess("WCP17InterParRoutingAdHoc", " A set of activity instances is executed sequentially in an\n" +
                    "order that is decided at run time. No two activity instances of this set are\n" +
                    "active at the same point in time",
            new BPMNTestCase().assertTask1().assertTask2());


    public static final BPMNProcess MILESTONE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP18Milestone", "An activity is only enabled\n" +
                    "if a certain milestone has been reached that has not expired yet (Weske 2012)",
            new BPMNTestCase().assertTask1().assertTask2());

    public static final BPMNProcess CANCEL_TASK_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP19CancelTask",
            "An activity can be canceled when it emits an error event.",
            new BPMNTestCase().assertTask2());

    public static final BPMNProcess CANCEL_CASE_PATTERN_ERROR = BPMNProcessBuilder.buildPatternProcess("WCP20CancelCaseError",
            "Cancels a sub-process by emitting an error event inside the sub-process which is handled through a boundary event. See Error_BoundaryEvent_SubProcess_Interrupting",
            new BPMNTestCase().assertTask1().assertTask2());

    public static final BPMNProcess CANCEL_CASE_PATTERN_CANCEL = BPMNProcessBuilder.buildPatternProcess("WCP20CancelCaseCancel",
            "Cancels a sub-process by emitting a cancel event inside the sub-process which is handled through a boundary event. See Cancel_Event",
            new BPMNTestCase().assertTask1().assertTask2());

    public static final BPMNProcess CANCEL_CASE_PATTERN_TERMINATE = BPMNProcessBuilder.buildPatternProcess("WCP20CancelCaseTerminate",
            "Cancels a process immediatly by emitting a terminate event. See Terminate_Event",
            new BPMNTestCase());

    public static final List<BPMNProcess> PATTERNS = Arrays.asList(
            WCP01_SEQUENCE,

            //WCP02
            WCP02_PARALLEL_SPLIT,

            //WCP03
            WCP03_SYNCHRONIZATION,

            //WCP04
            WCP04_EXCLUSIVE_CHOICE,

            //WCP05
            WCP05_SIMPLE_MERGE,

            //WCP06
            WCP06_MULTI_CHOICE,

            //WCP07
            WCP07_STRUCTURED_SYNCHRONIZING_MERGE,

            //WCP08
            WCP08_MULTI_MERGE,

            //WCP09
            DISCRIMINATOR_PATTERN,

            //WCP10
            ARBITRARY_PATTERN,
            ARBITRARY_PATTERN_2,

            //WCP11
            IMPLICIT_TERMINATION,

            //WCP12
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION,

            //WCP13
            MULTIPLE_INSTANCES_WITH_A_PRIORI_DESIGN_TIME_KNOWLEDGE,

            //WCP14
            MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE,

            //WCP16
            DEFERRED_CHOICE_PATTERN,

            //WCP17
            INTER_PAR_ROUTING_PATTERN,
            INTER_PAR_ROUTING_PATTERN_AD_HOC,

            //WCP18
            MILESTONE_PATTERN,

            //WCP19
            CANCEL_TASK_PATTERN,

            //WCP20 here
            CANCEL_CASE_PATTERN_ERROR,
            CANCEL_CASE_PATTERN_CANCEL,
            CANCEL_CASE_PATTERN_TERMINATE

    );
}