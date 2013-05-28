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
            "WCP01-SequencePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process PARALLEL_SPLIT_PATTERN = buildPatternProcess(
            "WCP02-ParallelSplitPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SYNCHRONIZATION_PATTERN = buildPatternProcess(
            "WCP03-SynchronizationPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process EXCLUSIVE_CHOICE_PATTERN = buildPatternProcess(
            "WCP04-ExclusiveChoicePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1A", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "11", stringOperationOutput: "11B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SIMPLE_MERGE_PATTERN = buildPatternProcess(
            "WCP05-SimpleMergePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1A", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "11", stringOperationOutput: "11B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MULTI_CHOICE_PATTERN = buildPatternProcess(
            "WCP06-MultiChoicePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "AYZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "ABZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "ABC", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MULTI_CHOICE_PATTERN_PARTIAL = buildPatternProcess(
            "WCP06-MultiChoicePattern-Partial",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "AYZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "ABZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "ABC", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SYNCHRONIZING_MERGE_PATTERN = buildPatternProcess(
            "WCP07-SynchronizingMergePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "AYZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "ABZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "ABC", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SYNCHRONIZING_MERGE_PATTERN_PARTIAL = buildPatternProcess(
            "WCP07-SynchronizingMergePattern-Partial",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "AYZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "ABZ", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "ABC", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process DISCRIMINATOR_PATTERN = buildPatternProcess(
            "WCP09-DiscriminatorPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process ARBITRARY_CYCLES_PATTERN = buildPatternProcess(
            "WCP10-ArbitraryCyclesPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1ABC", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "2", stringOperationOutput: "2BC", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "3", stringOperationOutput: "3C", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process IMPLICIT_TERMINATION_PATTERN = buildPatternProcess(
            "WCP11-ImplicitTerminationPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process DEFERRED_CHOICE_PATTERN = buildPatternProcess(
            "WCP16-DeferredChoicePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)]),
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronizationPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "1", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 2)]),
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "2", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 3)]),
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronizationPattern-Partial",
            [
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "100", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 4)])
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL = buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledgePattern-Partial",
            [
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "100", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 4)])
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledgePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "1", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 4)]),
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "2", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 4)]),
            ]
    )


    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledgePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "1", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 2)]),
                    new TestCase(testSteps: [new TestStep(input: "102", testPartner: true),
                            new TestStep(input: "2", operation: WsdlOperation.SYNC),
                            new TestStep(input: "101", testPartner: true, concurrencyTest: true),
                            new TestStep(input: "102", testPartner: true, partnerOutput: 3)]),
            ]
    )

    public final Process CANCEL_ACTIVITY_PATTERN = buildPatternProcess(
            "WCP19-CancelActivityPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1A", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "0", stringOperationOutput: "0B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process CANCEL_CASE_PATTERN = buildPatternProcess(
            "WCP20-CancelCasePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "0", assertions: [new ExitAssertion()], operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process MILESTONE_PATTERN = buildPatternProcess(
            "WCP18-MilestonePattern",
            [
                    new TestCase(name: "PickAsyncMessage", testSteps: [
                            new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC),
                            new TestStep(input: "1", operation: WsdlOperation.ASYNC),
                            new TestStep(input: "1", stringOperationOutput: "8", operation: WsdlOperation.SYNC_STRING),
                    ]),
                    new TestCase(name: "Pick3sTimeout", testSteps: [
                            new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC, timeToWaitAfterwards: 4000),
                            new TestStep(input: "1", stringOperationOutput: "9", operation: WsdlOperation.SYNC_STRING),
                    ])
            ]
    )

    public final Process INTERLEAVED_PARALLEL_ROUTING_PATTERN = buildPatternProcess(
            "WCP17-InterleavedParallelRoutingPattern",
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
            MULTI_CHOICE_PATTERN_PARTIAL,
            SYNCHRONIZING_MERGE_PATTERN,
            SYNCHRONIZING_MERGE_PATTERN_PARTIAL,
            DISCRIMINATOR_PATTERN,
            ARBITRARY_CYCLES_PATTERN,
            IMPLICIT_TERMINATION_PATTERN,
            DEFERRED_CHOICE_PATTERN,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN,
            CANCEL_ACTIVITY_PATTERN,
            CANCEL_CASE_PATTERN,
            MILESTONE_PATTERN,
            INTERLEAVED_PARALLEL_ROUTING_PATTERN
    ].flatten() as List<Process>


}
