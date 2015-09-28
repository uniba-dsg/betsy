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


    public static final BPMNProcess MERGE_PATTERN_IMPLICIT = BPMNProcessBuilder.buildPatternProcess("WCP05SimpleMergeImplicit",
            "A Process for Merging multiple branches into a single branch without using a converging XOR gateway, the test checks for single activation of a Task whenever a Token arrives",
            new BPMNTestCase().inputA().assertTask1().assertTask4(),
            new BPMNTestCase().inputB().assertTask2().assertTask4(),
            new BPMNTestCase().inputC().assertTask3().assertTask4(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4().assertTask4(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4()
    );


    public static final BPMNProcess MERGE_PATTERN_WITH_GATEWAY = BPMNProcessBuilder.buildPatternProcess("WCP05SimpleMergeWithGateway",
            "A Process for Merging multiple branches into a single branch with using a converging XOR gateway, the test checks for single activation of a Task whenever a Token arrives",
            new BPMNTestCase().inputA().assertTask1().assertTask4(),
            new BPMNTestCase().inputB().assertTask2().assertTask4(),
            new BPMNTestCase().inputC().assertTask3().assertTask4(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4().assertTask4(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4()
    );

    public static final BPMNProcess MULTI_CHOICE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP06MultiChoice", "A Process with the divergence of the thread of control " +
                    "into several parallel branches on a selective basis",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2(),
            new BPMNTestCase().inputC().assertTask3()
    );


    public static final BPMNProcess SYNC_MERGE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP07StructuredSynchronizedMerge", "A Process with the synchronised convergence of " +
                    "two or more alternative branches",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4()
    );


    public static final BPMNProcess MULTI_MERGE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP08MultiMerge", "A Process with the convergence of two or more branches " +
                    "into  a  single  path without synchronization",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4()
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

    public static final BPMNProcess MULTIPLE_INSTANCES_SYNCH_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP12MultipleInstancesWOSynchronization", "A Process which creates multiple activity instances of one activity model (Weske2012)",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask2());


    public static final BPMNProcess MULTIPLE_INSTANCES_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP13MultipleInstancesWithAPrioriDesignTimeKnowledge", "A Multiple Instances Process where the process execution loop cardinality is known beforehand during DesignTime",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final BPMNProcess MI_APRIORI_RUNTIME_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP14MIAPrioriRuntimeKnowledge", "A Process where the loop cardinality is known only during Runtime, this is achieved by \n" +
                    "checking the NrOfCompletedInstances against the total Number of possible instances",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask1().assertTask1().assertTask2());


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

    public static final BPMNProcess CANCEL_TASK_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP19CancelTask", "A process with  the  ability  to  depict  that  an  enabled  activity  should  be\n" +
                    "disabled in some nominated circumstance.",
            new BPMNTestCase().assertTask2());

    public static final BPMNProcess CANCEL_CASE_PATTERN_ERROR = BPMNProcessBuilder.buildPatternProcess("WCP20CancelCaseError",
            "The Cancel Case pattern describes the removal of a complete process instance. (Bizagi)",
            new BPMNTestCase().assertTask1().assertTask2());

    public static final BPMNProcess CANCEL_CASE_PATTERN_CANCEL = BPMNProcessBuilder.buildPatternProcess("WCP20CancelCaseCancel",
            "The Cancel Case pattern describes the removal of a complete process instance. (Bizagi)",
            new BPMNTestCase().assertTask1().assertTask2());

    public static final BPMNProcess CANCEL_CASE_PATTERN_TERMINATE = BPMNProcessBuilder.buildPatternProcess("WCP20CancelCaseTerminate",
            "The Cancel Case pattern describes the removal of a complete process instance. (Bizagi)",
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
            MERGE_PATTERN_IMPLICIT,
            MERGE_PATTERN_WITH_GATEWAY,

            //WCP06
            MULTI_CHOICE_PATTERN,

            //WCP07
            SYNC_MERGE_PATTERN,

            //WCP08
            MULTI_MERGE_PATTERN,

            //WCP09
            DISCRIMINATOR_PATTERN,

            //WCP10
            ARBITRARY_PATTERN,
            ARBITRARY_PATTERN_2,

            //WCP11
            IMPLICIT_TERMINATION,

            //WCP12
            MULTIPLE_INSTANCES_SYNCH_PATTERN,

            //WCP13
            MULTIPLE_INSTANCES_PATTERN,

            //WCP14
            MI_APRIORI_RUNTIME_PATTERN,

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