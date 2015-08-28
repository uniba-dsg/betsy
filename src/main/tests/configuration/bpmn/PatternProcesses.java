package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Created by stssobetzko on 12.06.2015.
 */
public class PatternProcesses {

    public static final BPMNProcess SEQUENCE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP01SequenceFlow", "A Process for a sequence flow with a single start and end task which assert if the Process has been executed once",
            new BPMNTestCase().assertTask1());


    public static final BPMNProcess PARALLEL_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP02ParallelSplit", "A Process for a parallel Split with 3 tasks , each task has to be executed exactly once",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());


    public static final BPMNProcess SYNCHRONIZATION_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP03Synchronization", "A Process for Synchronising 3 branches into a single branch, Task4 should be only executed once if the synchronization is working correctly",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4());


    public static final BPMNProcess EXCLUSIVE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP04ExclusiveChoice", "A Process for a decision based workflow, if the process contains a it should execute Task1, if it contains b it should execute Task2, if neither of these is executed, the Default Task should be executed",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB().assertTask2(),
            new BPMNTestCase().inputC().assertTask3()
    );


    //This pattern will most likely not run on jBPM due to it's constraints of having only one incoming flow to a single task according to log
     public static final BPMNProcess MERGE_PATTERN_IMPLICIT = BPMNProcessBuilder.buildPatternProcess("WCP05SimpleMergeImplicit",
             "A Process for Merging multiple branches into a single branch without using a converging XOR gateway, the test checks for single activation of a Task whenever a Token arrives",
             new BPMNTestCase().inputA().assertTask1().assertTask4(),
             new BPMNTestCase().inputB().assertTask2().assertTask4(),
             new BPMNTestCase().inputC().assertTask3().assertTask4(),



            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4().assertTask4(),
            new BPMNTestCase().inputA().inputB().assertTask1().assertTask2().assertTask4(),
            //TODO See why the AA case only activates once, this seems to be more a Test routine problem than an engine specific behaviour
          //  new BPMNTestCase().inputAA().assertTask1().assertTask1().assertTask4().assertTask4(),
             new BPMNTestCase().inputABC().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4()
     );



    //TODO Add the Merge Pattern with XOR Gateway and test accordingly
    public static final BPMNProcess MERGE_PATTERN_WITH_GATEWAY = BPMNProcessBuilder.buildPatternProcess("WCP05SimpleMergeWithGateway",
            "A Process for Merging multiple branches into a single branch with using a converging XOR gateway, the test checks for single activation of a Task whenever a Token arrives",
            new BPMNTestCase().inputA().assertTask1().assertTask4(),
            new BPMNTestCase().inputB().assertTask2().assertTask4(),
            new BPMNTestCase().inputC().assertTask3().assertTask4(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4().assertTask4(),
            //TODO See why the AA case only activates once, this seems to be more a Test routine problem than an engine specific behaviour
            //new BPMNTestCase().inputAA().assertTask1().assertTask1().assertTask4().assertTask4(),
            new BPMNTestCase().inputABC().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4()
    );



    public static final BPMNProcess MULTI_CHOICE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP06MultiChoice", "A Process with the divergence of the thread of control " +
                    "into several parallel branches on a selective basis",
            new BPMNTestCase().inputA().assertTask1().assertTask3(),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask3(),
            new BPMNTestCase().inputC().assertTask3()
    );


    //TODO check for validity of test logic
    public static final BPMNProcess SYNC_MERGE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP07StructuredSynchronizedMerge", "A Process with the synchronised convergence of " +
                    "two or more alternative branches",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4()
            );


    public static final BPMNProcess MULTI_MERGE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP08MultiMerge", "A Process with the convergence of two or more branches " +
                    "into  a  single  path without synchronization",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4()
    );


    //TODO: Adapt complex gateway to continue when one task has completed
    public static final BPMNProcess DISCRIMINATOR_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP09Discriminator", "A point in the workflow process that waits for one of the" +
            " incoming branches to complete before activating the subsequent activity"+"this is achieved by an N out of M MultiInstance join that completes after ONE instance as depicted in Wohed2005",
            new BPMNTestCase().assertTask1());

    //TODO: Add test case for WCP10 here



    public static final BPMNProcess MULTIPLE_INSTANCES_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP13MultipleInstancesWithAPrioriDesignTimeKnowledge", "A Process where the process execution loop cardinality is known beforehand during DesignTime",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask1().assertTask1().assertTask2());

    public static final BPMNProcess MI_APRIORI_RUNTIME_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP14MIAPrioriRuntimeKnowledge", "A Process where the execution cardinality is known only during Runtime, this is achieved by the expression\n"+
                    "(nrOfCompletedInstances/nrOfInstances >= 0.6)",
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask1().assertTask1().assertTask2());


    // simply tests whether all activities are executed (even though an end event might be reached on another path before)
    public static final BPMNProcess TERMINATION_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP11ImplicitTermination","A process with the ability to depict the notion that a given sub-process\n" +
            "should be terminated when there are no remaining activities to be completed.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask2());




    public static final BPMNProcess DEFERRED_CHOICE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP16DeferredChoice", "A process with the ability to depict a divergence point in a process where\n" +
            "one of several possible branches should be activated.",
            new BPMNTestCase().assertTask1().optionDelay(5000));


    //Note: doesn't work with jbpm due to MI usage
    //TODO: write case for jbpm
    public static final BPMNProcess INTER_PAR_ROUTING_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP17InterleavedParallelRouting", " A set of activity instances is executed sequentially in an\n" +
            "order that is decided at run time. No two activity instances of this set are\n" +
            "active at the same point in time",
            new BPMNTestCase().assertTask1().assertTask2().assertTask2().assertTask2());

    //TODO: make conditional boundary event work
    public static final BPMNProcess CANCEL_TASK_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP19CancelTask", "A process with  the  ability  to  depict  that  an  enabled  activity  should  be\n" +
            "disabled in some nominated circumstance",
            new BPMNTestCase().inputA().assertTask1().assertTask2());

    public static final List<BPMNProcess> PATTERNS = Arrays.asList(
            SEQUENCE_PATTERN,
            PARALLEL_PATTERN,
            SYNCHRONIZATION_PATTERN,
            EXCLUSIVE_PATTERN,
            MERGE_PATTERN_IMPLICIT,
            MERGE_PATTERN_WITH_GATEWAY,
            MULTI_CHOICE_PATTERN,
            SYNC_MERGE_PATTERN,
            MULTI_MERGE_PATTERN,
            DISCRIMINATOR_PATTERN,
            // WCP10 here
            TERMINATION_PATTERN,
            // WCP12 here
            MULTIPLE_INSTANCES_PATTERN,
            MI_APRIORI_RUNTIME_PATTERN,
            //WCP15 is not implementable?
            DEFERRED_CHOICE_PATTERN,
            INTER_PAR_ROUTING_PATTERN,
            //WCP18 here
            CANCEL_TASK_PATTERN
    ); 
}