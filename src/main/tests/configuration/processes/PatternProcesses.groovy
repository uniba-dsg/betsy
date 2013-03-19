package configuration.processes

import betsy.data.Process
import betsy.data.TestCase
import betsy.data.TestStep
import betsy.data.WsdlOperation
import betsy.data.assertions.ExitAssertion

/**
 * Created with IntelliJ IDEA.
 * User: joerg
 * Date: 18.03.13
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
class PatternProcesses {

    private Process buildPatternProcess(String name, List<TestCase> testCases) {
        new Process(bpel: "patterns/control-flow/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                testCases: testCases
        )
    }

    private Process buildPatternProcessWithPartner(String name, List<TestCase> testCases) {
        new Process(bpel: "patterns/control-flow/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl", "language-features/TestPartner.wsdl"],
                testCases: testCases
        )
    }

    public final Process SEQUENCE_PATTERN = buildPatternProcess(
            "SequencePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process PARALLEL_SPLIT_PATTERN = buildPatternProcess(
            "ParallelSplitPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SYNCHRONIZATION_PATTERN = buildPatternProcess(
            "SynchronizationPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process EXCLUSIVE_CHOICE_PATTERN = buildPatternProcess(
            "ExclusiveChoicePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1A", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "11", stringOperationOutput: "1B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SIMPLE_MERGE_PATTERN = buildPatternProcess(
            "SimpleMergePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1A", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "11", stringOperationOutput: "1B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MULTI_CHOICE_PATTERN = buildPatternProcess(
            "MultiChoicePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "AYZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "ABZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "ABC", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SYNCHRONIZING_MERGE_PATTERN = buildPatternProcess(
            "SynchronizingMergePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "AYZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "ABZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "ABC", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process DISCRIMINATOR_PATTERN = buildPatternProcess(
            "DiscriminatorPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process ARBITRARY_CYCLES_PATTERN = buildPatternProcess(
            "ArbitraryCyclesPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1ABC", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "2BC", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "3C", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process IMPLICIT_TERMINATION_PATTERN = buildPatternProcess(
            "ImplicitTerminationPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process DEFERRED_CHOICE_PATTERN = buildPatternProcess(
            "DeferredChoicePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)]),
                    new TestCase(testSteps: [new TestStep(input: "1", operation: WsdlOperation.ASYNC)])
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN = buildPatternProcessWithPartner(
            "MultipleInstancesWithoutSynchronizationPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "2", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "3", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "MultipleInstancesWithAPrioriDesignTimeKnowledgePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "2", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "3", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "MultipleInstancesWithAPrioriRuntimeKnowledgePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "2", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "3", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process CANCEL_ACTIVITY_PATTERN = buildPatternProcess(
            "CancelActivityPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1A", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "0", stringOperationOutput: "0B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process CANCEL_CASE_PATTERN = buildPatternProcess(
            "CancelCasePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "0", assertions: [new ExitAssertion()], operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MILESTONE_PATTERN = buildPatternProcess(
            "MilestonePattern",
            [
                    new TestCase(testSteps: [
                            new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC),
                            new TestStep(input: "1", operation: WsdlOperation.ASYNC),
                            new TestStep(input: "1", output: "8", operation: WsdlOperation.SYNC),
                    ]),
                    new TestCase(testSteps: [
                            new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC, timeToWaitAfterwards: 4000),
                            new TestStep(input: "1", output: "9", operation: WsdlOperation.SYNC),
                    ])
            ]
    )

    public final Process INTERLEAVED_PARALLEL_ROUTING_PATTERN = buildPatternProcess(
            "InterleavedParallelRoutingPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "AW1ABW2B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final List<Process> CONTROL_FLOW_PATTERNS = [
           SEQUENCE_PATTERN,
           PARALLEL_SPLIT_PATTERN,
           SYNCHRONIZATION_PATTERN,
           EXCLUSIVE_CHOICE_PATTERN,
           SIMPLE_MERGE_PATTERN,
           MULTI_CHOICE_PATTERN,
           SYNCHRONIZING_MERGE_PATTERN,
           DISCRIMINATOR_PATTERN,
           ARBITRARY_CYCLES_PATTERN,
           IMPLICIT_TERMINATION_PATTERN,
           DEFERRED_CHOICE_PATTERN,
           MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN,
           MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN,
           MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN,
           CANCEL_ACTIVITY_PATTERN,
           CANCEL_CASE_PATTERN,
           MILESTONE_PATTERN,
           INTERLEAVED_PARALLEL_ROUTING_PATTERN
    ].flatten() as List<Process>


}
