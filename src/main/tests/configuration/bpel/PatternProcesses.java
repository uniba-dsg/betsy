package configuration.bpel;

import betsy.bpel.model.BPELTestCase;
import betsy.bpel.model.assertions.ExitAssertion;
import betsy.common.model.feature.FeatureSet;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.feature.Feature;

import java.util.Arrays;
import java.util.List;

class PatternProcesses {

    public static final EngineIndependentProcess SEQUENCE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP01-Sequence",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP01_Sequence"), "WCP01-Sequence", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
    );

    public static final EngineIndependentProcess PARALLEL_SPLIT_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP02-ParallelSplit",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP02_ParallelSplit"), "WCP02-ParallelSplit", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
    );

    public static final EngineIndependentProcess SYNCHRONIZATION_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP03-Synchronization",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP03_Synchronization"), "WCP03-Synchronization", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
    );

    public static final EngineIndependentProcess EXCLUSIVE_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP04-ExclusiveChoice",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP04_ExclusiveChoice"), "WCP04-ExclusiveChoice", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
            new BPELTestCase().checkDeployment().sendSyncString(11, "11B")
    );

    public static final EngineIndependentProcess SIMPLE_MERGE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP05-SimpleMerge",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP05_SimpleMerge"), "WCP05-SimpleMerge", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
            new BPELTestCase().checkDeployment().sendSyncString(11, "11B")
    );

    public static final EngineIndependentProcess MULTI_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP06-MultiChoice",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP06_MultiChoice"), "WCP06-MultiChoice", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final EngineIndependentProcess MULTI_CHOICE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcess(
            "WCP06-MultiChoice-Partial",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP06_MultiChoice"), "WCP06-MultiChoice-Partial", "", "+/-"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final EngineIndependentProcess SYNCHRONIZING_MERGE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP07-SynchronizingMerge",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP07_SynchronizingMerge"), "WCP07-SynchronizingMerge", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final EngineIndependentProcess SYNCHRONIZING_MERGE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcess(
            "WCP07-SynchronizingMerge-Partial",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP07_SynchronizingMerge"), "WCP07-SynchronizingMerge-Partial", "", "+/-"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final EngineIndependentProcess IMPLICIT_TERMINATION_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP11-ImplicitTermination",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP11_ImplicitTermination"), "WCP11-ImplicitTermination", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1")
    );

    public static final EngineIndependentProcess DEFERRED_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP16-DeferredChoice",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP16_DeferredChoice"), "WCP16-DeferredChoice", "", "+"),
            new BPELTestCase().checkDeployment().sendSync(1, 1),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1")
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP12_MultipleInstancesWithoutSynchronization"), "WCP12-MultipleInstancesWithoutSynchronization", "", "+"),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(2),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Partial",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP12_MultipleInstancesWithoutSynchronization"), "WCP12-MultipleInstancesWithoutSynchronization-Partial", "", "+/-"),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().
                    sendSync(100).assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_SYNC = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Sync",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP12_MultipleInstancesWithoutSynchronization"), "WCP12-MultipleInstancesWithoutSynchronization-Sync", "", "+"),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(2),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_SYNC_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Sync-Partial",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP12_MultipleInstancesWithoutSynchronization"), "WCP12-MultipleInstancesWithoutSynchronization-Sync-Partial", "", "+/-"),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().
                    sendSync(100).assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-While-Partial",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP12_MultipleInstancesWithoutSynchronization"), "WCP12-MultipleInstancesWithoutSynchronization-While-Partial", "", "+/-"),
            new BPELTestCase().checkDeployment().sendSync(1, 1),
            new BPELTestCase().checkDeployment().sendSync(2, 2)
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_SYNC_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-While-Sync-Partial",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP12_MultipleInstancesWithoutSynchronization"), "WCP12-MultipleInstancesWithoutSynchronization-While-Sync-Partial", "", "+/-"),
            new BPELTestCase().checkDeployment().sendSync(1, 1),
            new BPELTestCase().checkDeployment().sendSync(2, 2)
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge-Partial",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge"), "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge-Partial", "", "+/-"),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(100).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );

    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge"), "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge", "", "+"),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );


    public static
    final EngineIndependentProcess MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledge",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP14_MultipleInstancesWithAPrioriRuntimeKnowledge"), "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledge", "", "+"),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(2),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static final EngineIndependentProcess CANCEL_ACTIVITY_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP19-CancelActivity",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP19_CancelActivity"), "WCP19-CancelActivity", "", "+/-"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
            new BPELTestCase().checkDeployment().sendSyncString(0, "0B")
    );

    public static final EngineIndependentProcess CANCEL_CASE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP20-CancelCase",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP20_CancelCase"), "WCP20-CancelCase", "", "+"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1"),
            new BPELTestCase().checkDeployment().sendSyncString(0, new ExitAssertion())
    );

    public static final EngineIndependentProcess MILESTONE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP18-Milestone",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP18_Milestone"), "WCP18-Milestone", "", "+/-"),
            new BPELTestCase("PickAsyncMessage").checkDeployment().sendSync(1, 1).sendAsync(1).sendSyncString(1, "8"),
            new BPELTestCase("Pick3sTimeout").checkDeployment().sendSync(1, 1).waitFor(4_000).sendSyncString(1, "9")
    );

    public static final EngineIndependentProcess INTERLEAVED_PARALLEL_ROUTING_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP17-InterleavedParallelRouting",
            new Feature(new FeatureSet(Groups.CFPATTERNS, "WCP17_InterleavedParallelRouting"), "WCP17-InterleavedParallelRouting", "", "+/-"),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AW1ABW2B")
    );

    public static final List<EngineIndependentProcess> CONTROL_FLOW_PATTERNS = Arrays.asList(
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
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_SYNC,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_SYNC_PARTIAL,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_PARTIAL,
            MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_SYNC_PARTIAL,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL,
            MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN,
            CANCEL_ACTIVITY_PATTERN,
            CANCEL_CASE_PATTERN,
            MILESTONE_PATTERN,
            INTERLEAVED_PARALLEL_ROUTING_PATTERN
    );


}
