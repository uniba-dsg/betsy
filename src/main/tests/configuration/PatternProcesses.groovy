package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.data.assertions.ExitAssertion

import static configuration.ProcessBuilder.buildPatternProcess
import static configuration.ProcessBuilder.buildPatternProcessWithPartner


class PatternProcesses {

    public static final BetsyProcess SEQUENCE_PATTERN = buildPatternProcess(
            "WCP01-Sequence",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public static final BetsyProcess PARALLEL_SPLIT_PATTERN = buildPatternProcess(
            "WCP02-ParallelSplit",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public static final BetsyProcess SYNCHRONIZATION_PATTERN = buildPatternProcess(
            "WCP03-Synchronization",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public static final BetsyProcess EXCLUSIVE_CHOICE_PATTERN = buildPatternProcess(
            "WCP04-ExclusiveChoice",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1A"),
                    new TestCase().checkDeployment().sendSyncString(11, "11B")
            ]
    )

    public static final BetsyProcess SIMPLE_MERGE_PATTERN = buildPatternProcess(
            "WCP05-SimpleMerge",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1A"),
                    new TestCase().checkDeployment().sendSyncString(11, "11B")
            ]
    )

    public static final BetsyProcess MULTI_CHOICE_PATTERN = buildPatternProcess(
            "WCP06-MultiChoice",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BetsyProcess MULTI_CHOICE_PATTERN_PARTIAL = buildPatternProcess(
            "WCP06-MultiChoice-Partial",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BetsyProcess SYNCHRONIZING_MERGE_PATTERN = buildPatternProcess(
            "WCP07-SynchronizingMerge",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BetsyProcess SYNCHRONIZING_MERGE_PATTERN_PARTIAL = buildPatternProcess(
            "WCP07-SynchronizingMerge-Partial",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new TestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new TestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BetsyProcess IMPLICIT_TERMINATION_PATTERN = buildPatternProcess(
            "WCP11-ImplicitTermination",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1")
            ]
    )

    public static final BetsyProcess DEFERRED_CHOICE_PATTERN = buildPatternProcess(
            "WCP16-DeferredChoice",
            [
                    new TestCase().checkDeployment().sendSync(1, 1),
                    new TestCase().checkDeployment().sendSyncString(1, "1")
            ]
    )

    public static final BetsyProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(2),
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(3)
            ]
    )

    public static final BetsyProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Partial",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().
                            sendSync(100).buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )

    public static final BetsyProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_PARTIAL = buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-While-Partial",
            [
                    new TestCase().checkDeployment().sendSync(1, 1),
                    new TestCase().checkDeployment().sendSync(2, 2)
            ]
    )

    public static final BetsyProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL = buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge-Partial",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(100).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )

    public static final BetsyProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4),
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )


    public static final BetsyProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN = buildPatternProcessWithPartner(
            "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledge",
            [
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(2),
                    new TestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(3)
            ]
    )

    public static final BetsyProcess CANCEL_ACTIVITY_PATTERN = buildPatternProcess(
            "WCP19-CancelActivity",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1A"),
                    new TestCase().checkDeployment().sendSyncString(0, "0B")
            ]
    )

    public static final BetsyProcess CANCEL_CASE_PATTERN = buildPatternProcess(
            "WCP20-CancelCase",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "1"),
                    new TestCase().checkDeployment().sendSyncString(0, new ExitAssertion())
            ]
    )

    public static final BetsyProcess MILESTONE_PATTERN = buildPatternProcess(
            "WCP18-Milestone",
            [
                    new TestCase(name: "PickAsyncMessage").checkDeployment().sendSync(1, 1).
                            sendAsync(1).sendSyncString(1, "8"),
                    new TestCase(name: "Pick3sTimeout").checkDeployment().sendSync(1, 1).
                            waitFor(4000).sendSyncString(1, "9")
            ]
    )

    public static final BetsyProcess INTERLEAVED_PARALLEL_ROUTING_PATTERN = buildPatternProcess(
            "WCP17-InterleavedParallelRouting",
            [
                    new TestCase().checkDeployment().sendSyncString(1, "AW1ABW2B")
            ]
    )

    public static final List<BetsyProcess> CONTROL_FLOW_PATTERNS = [
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
    ].flatten() as List<BetsyProcess>


}
