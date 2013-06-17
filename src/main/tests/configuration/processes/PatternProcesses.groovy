package configuration.processes

import betsy.data.Process
import betsy.data.TestCase
import betsy.data.assertions.ExitAssertion


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
                    new TestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public final Process PARALLEL_SPLIT_PATTERN = buildPatternProcess(
            "WCP02-ParallelSplitPattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public final Process SYNCHRONIZATION_PATTERN = buildPatternProcess(
            "WCP03-SynchronizationPattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public final Process EXCLUSIVE_CHOICE_PATTERN = buildPatternProcess(
            "WCP04-ExclusiveChoicePattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"1A"),
                    new TestCase().checkDeployment().sendSyncString(11,"11B")
            ]
    )

    public final Process SIMPLE_MERGE_PATTERN = buildPatternProcess(
            "WCP05-SimpleMergePattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"1A"),
                    new TestCase().checkDeployment().sendSyncString(11,"11B")
            ]
    )

    public final Process MULTI_CHOICE_PATTERN = buildPatternProcess(
            "WCP06-MultiChoicePattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2,"ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3,"ABC")
            ]
    )

    public final Process MULTI_CHOICE_PATTERN_PARTIAL = buildPatternProcess(
            "WCP06-MultiChoicePattern-Partial",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2,"ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3,"ABC")
            ]
    )

    public final Process SYNCHRONIZING_MERGE_PATTERN = buildPatternProcess(
            "WCP07-SynchronizingMergePattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2,"ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3,"ABC")
            ]
    )

    public final Process SYNCHRONIZING_MERGE_PATTERN_PARTIAL = buildPatternProcess(
            "WCP07-SynchronizingMergePattern-Partial",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2,"ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3,"ABC")
            ]
    )

    public final Process IMPLICIT_TERMINATION_PATTERN = buildPatternProcess(
            "WCP11-ImplicitTerminationPattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1")
            ]
    )

    public final Process DEFERRED_CHOICE_PATTERN = buildPatternProcess(
            "WCP16-DeferredChoicePattern",
            [
                    new TestCase().checkDeployment().sendSync(1, 1),
                    new TestCase().checkDeployment().sendSyncString(1, "1")
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronizationPattern",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(2),
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(3)
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronizationPattern-Partial",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().
                            sendSync(100).buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_PARTIAL = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronizationPattern-While-Partial",
            [
                    new TestCase().checkDeployment().sendSync(1,1),
                    new TestCase().checkDeployment().sendSync(2,2)
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL = buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledgePattern-Partial",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(100).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )

    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledgePattern",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4),
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )


    public final Process MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledgePattern",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(2),
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(3)
            ]
    )

    public final Process CANCEL_ACTIVITY_PATTERN = buildPatternProcess(
            "WCP19-CancelActivityPattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"1A"),
                    new TestCase().checkDeployment().sendSyncString(0,"0B")
            ]
    )

    public final Process CANCEL_CASE_PATTERN = buildPatternProcess(
            "WCP20-CancelCasePattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1,"1"),
                    new TestCase().checkDeployment().sendSyncString(0, new ExitAssertion())
            ]
    )

    public final Process MILESTONE_PATTERN = buildPatternProcess(
            "WCP18-MilestonePattern",
            [
                    new TestCase(name: "PickAsyncMessage").checkDeployment().sendSync(1, 1).
                            sendAsync(1).sendSyncString(1, "8"),
                    new TestCase(name: "Pick3sTimeout").checkDeployment().sendSync(1,1).
                            waitFor(4000).sendSyncString(1,"9")
            ]
    )

    public final Process INTERLEAVED_PARALLEL_ROUTING_PATTERN = buildPatternProcess(
            "WCP17-InterleavedParallelRoutingPattern",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "AW1ABW2B")
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
            IMPLICIT_TERMINATION_PATTERN,
            DEFERRED_CHOICE_PATTERN,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_PARTIAL,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN,
            CANCEL_ACTIVITY_PATTERN,
            CANCEL_CASE_PATTERN,
            MILESTONE_PATTERN,
            INTERLEAVED_PARALLEL_ROUTING_PATTERN
    ].flatten() as List<Process>


}
