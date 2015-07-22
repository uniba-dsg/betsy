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

    //TODO This should be thought over again, maybe think of a new way to test for non-parallel execution?
    public static final BPMNProcess MERGE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP05SimpleMerge",
            "A Process for Merging 3 branches into a single branch without using a converging gateway,all of parallel Tasks will write the same assertion job but only one (namely the fastest to execute) of the referred task will enable the end Task, the other 2 are not need for activation and will therefore not show up in the assertion List",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4().assertTask4().assertTask4());


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
            " incoming branches to complete before activating the subsequent activity",
            new BPMNTestCase().assertTask1().assertTask2().assertTask4());

    //TODO: Add test case for WCP10 here

    // simply tests whether all activities are executed (even though one end event might be reached)
    public static final BPMNProcess TERMINATION_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP11ImplicitTermination","A process with the ability to depict the notion that a given sub-process\n" +
            "should be terminated when there are no remaining activities to be completed.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask2());


    public static final List<BPMNProcess> PATTERNS = Arrays.asList(
            SEQUENCE_PATTERN,
            PARALLEL_PATTERN,
            SYNCHRONIZATION_PATTERN,
            EXCLUSIVE_PATTERN,
            MERGE_PATTERN,
            MULTI_CHOICE_PATTERN,
            SYNC_MERGE_PATTERN,
            MULTI_MERGE_PATTERN,
            DISCRIMINATOR_PATTERN,
            // WCP10 here,
            TERMINATION_PATTERN

    ); 
}