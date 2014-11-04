package configuration.bpel

import betsy.bpel.model.BPELProcess
import betsy.bpel.model.BPELTestCase
import betsy.bpel.model.assertions.ExitAssertion

class PatternProcesses {

    public static final BPELProcess SEQUENCE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP01-Sequence",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public static final BPELProcess PARALLEL_SPLIT_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP02-ParallelSplit",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public static final BPELProcess SYNCHRONIZATION_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP03-Synchronization",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
            ]
    )

    public static final BPELProcess EXCLUSIVE_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP04-ExclusiveChoice",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
                    new BPELTestCase().checkDeployment().sendSyncString(11, "11B")
            ]
    )

    public static final BPELProcess SIMPLE_MERGE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP05-SimpleMerge",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
                    new BPELTestCase().checkDeployment().sendSyncString(11, "11B")
            ]
    )

    public static final BPELProcess MULTI_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP06-MultiChoice",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BPELProcess MULTI_CHOICE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcess(
            "WCP06-MultiChoice-Partial",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BPELProcess SYNCHRONIZING_MERGE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP07-SynchronizingMerge",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BPELProcess SYNCHRONIZING_MERGE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcess(
            "WCP07-SynchronizingMerge-Partial",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
                    new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
            ]
    )

    public static final BPELProcess IMPLICIT_TERMINATION_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP11-ImplicitTermination",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1")
            ]
    )

    public static final BPELProcess DEFERRED_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP16-DeferredChoice",
            [
                    new BPELTestCase().checkDeployment().sendSync(1, 1),
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1")
            ]
    )

    public static final BPELProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization",
            [
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(2),
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(3)
            ]
    )

    public static final BPELProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Partial",
            [
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().
                            sendSync(100).buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )

    public static final BPELProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-While-Partial",
            [
                    new BPELTestCase().checkDeployment().sendSync(1, 1),
                    new BPELTestCase().checkDeployment().sendSync(2, 2)
            ]
    )

    public static final BPELProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge-Partial",
            [
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(100).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )

    public static final BPELProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge",
            [
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4),
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(4)
            ]
    )


    public static final BPELProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledge",
            [
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(2),
                    new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                            buildPartnerConcurrencyCheck().buildPartnerValueCheck(3)
            ]
    )

    public static final BPELProcess CANCEL_ACTIVITY_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP19-CancelActivity",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
                    new BPELTestCase().checkDeployment().sendSyncString(0, "0B")
            ]
    )

    public static final BPELProcess CANCEL_CASE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP20-CancelCase",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "1"),
                    new BPELTestCase().checkDeployment().sendSyncString(0, new ExitAssertion())
            ]
    )

    public static final BPELProcess MILESTONE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP18-Milestone",
            [
                    new BPELTestCase(name: "PickAsyncMessage").checkDeployment().sendSync(1, 1).
                            sendAsync(1).sendSyncString(1, "8"),
                    new BPELTestCase(name: "Pick3sTimeout").checkDeployment().sendSync(1, 1).
                            waitFor(4000).sendSyncString(1, "9")
            ]
    )

    public static final BPELProcess INTERLEAVED_PARALLEL_ROUTING_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP17-InterleavedParallelRouting",
            [
                    new BPELTestCase().checkDeployment().sendSyncString(1, "AW1ABW2B")
            ]
    )

    public static final List<BPELProcess> CONTROL_FLOW_PATTERNS = [
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
    ].flatten() as List<BPELProcess>


}
