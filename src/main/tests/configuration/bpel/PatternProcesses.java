package configuration.bpel;

import betsy.bpel.model.BPELTestCase;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.assertions.AssertExit;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.feature.Feature;

import java.util.Arrays;
import java.util.List;

class PatternProcesses {

    public static final FeatureSet WCP01 = new FeatureSet(Groups.CFPATTERNS, "WCP01_Sequence");
    public static final FeatureSet WCP02 = new FeatureSet(Groups.CFPATTERNS, "WCP02_ParallelSplit");
    public static final FeatureSet WCP03 = new FeatureSet(Groups.CFPATTERNS, "WCP03_Synchronization");
    public static final FeatureSet WCP04 = new FeatureSet(Groups.CFPATTERNS, "WCP04_ExclusiveChoice");
    public static final FeatureSet WCP05 = new FeatureSet(Groups.CFPATTERNS, "WCP05_SimpleMerge");
    public static final FeatureSet WCP06 = new FeatureSet(Groups.CFPATTERNS, "WCP06_MultiChoice");
    public static final FeatureSet WCP07 = new FeatureSet(Groups.CFPATTERNS, "WCP07_SynchronizingMerge");
    public static final FeatureSet WCP11 = new FeatureSet(Groups.CFPATTERNS, "WCP11_ImplicitTermination");
    public static final FeatureSet WCP12 = new FeatureSet(Groups.CFPATTERNS, "WCP12_MultipleInstancesWithoutSynchronization");
    public static final FeatureSet WCP13 = new FeatureSet(Groups.CFPATTERNS, "WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge");
    public static final FeatureSet WCP14 = new FeatureSet(Groups.CFPATTERNS, "WCP14_MultipleInstancesWithAPrioriRuntimeKnowledge");
    public static final FeatureSet WCP16 = new FeatureSet(Groups.CFPATTERNS, "WCP16_DeferredChoice");
    public static final FeatureSet WCP17 = new FeatureSet(Groups.CFPATTERNS, "WCP17_InterleavedParallelRouting");
    public static final FeatureSet WCP18 = new FeatureSet(Groups.CFPATTERNS, "WCP18_Milestone");
    public static final FeatureSet WCP19 = new FeatureSet(Groups.CFPATTERNS, "WCP19_CancelActivity");

    public static final FeatureSet WCP20 = new FeatureSet(Groups.CFPATTERNS, "WCP20_CancelCase");
    public static final Test SEQUENCE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP01-Sequence",
            new Feature(WCP01, "WCP01-Sequence", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
    );

    public static final Test PARALLEL_SPLIT_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP02-ParallelSplit",
            new Feature(WCP02, "WCP02-ParallelSplit", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
    );

    public static final Test SYNCHRONIZATION_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP03-Synchronization",
            new Feature(WCP03, "WCP03-Synchronization", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1AB")
    );
    public static final Test EXCLUSIVE_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP04-ExclusiveChoice",
            new Feature(WCP04, "WCP04-ExclusiveChoice", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
            new BPELTestCase().checkDeployment().sendSyncString(11, "11B")
    );

    public static final Test SIMPLE_MERGE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP05-SimpleMerge",
            new Feature(WCP05, "WCP05-SimpleMerge", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
            new BPELTestCase().checkDeployment().sendSyncString(11, "11B")
    );

    public static final Test MULTI_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP06-MultiChoice",
            new Feature(WCP06, "WCP06-MultiChoice", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final Test MULTI_CHOICE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcess(
            "WCP06-MultiChoice-Partial",
            new Feature(WCP06, "WCP06-MultiChoice-Partial", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final Test SYNCHRONIZING_MERGE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP07-SynchronizingMerge",
            new Feature(WCP07, "WCP07-SynchronizingMerge", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final Test SYNCHRONIZING_MERGE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcess(
            "WCP07-SynchronizingMerge-Partial",
            new Feature(WCP07, "WCP07-SynchronizingMerge-Partial", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AYZ"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "ABZ"),
            new BPELTestCase().checkDeployment().sendSyncString(3, "ABC")
    );

    public static final Test IMPLICIT_TERMINATION_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP11-ImplicitTermination",
            new Feature(WCP11, "WCP11-ImplicitTermination", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1")
    );
    public static final Test DEFERRED_CHOICE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP16-DeferredChoice",
            new Feature(WCP16, "WCP16-DeferredChoice", ""),
            new BPELTestCase().checkDeployment().sendSync(1, 1),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1")
    );

    public static
    final Test MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization",
            new Feature(WCP12, "WCP12-MultipleInstancesWithoutSynchronization", ""),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(2),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Partial",
            new Feature(WCP12, "WCP12-MultipleInstancesWithoutSynchronization-Partial", ""),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().
                    sendSync(100).assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_SYNC = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Sync",
            new Feature(WCP12, "WCP12-MultipleInstancesWithoutSynchronization-Sync", ""),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(2),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_SYNC_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-Sync-Partial",
            new Feature(WCP12, "WCP12-MultipleInstancesWithoutSynchronization-Sync-Partial", ""),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().
                    sendSync(100).assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-While-Partial",
            new Feature(WCP12, "WCP12-MultipleInstancesWithoutSynchronization-While-Partial", ""),
            new BPELTestCase().checkDeployment().sendSync(1, 1),
            new BPELTestCase().checkDeployment().sendSync(2, 2)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITHOUT_SYNCHRONIZATION_PATTERN_WHILE_SYNC_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP12-MultipleInstancesWithoutSynchronization-While-Sync-Partial",
            new Feature(WCP12, "WCP12-MultipleInstancesWithoutSynchronization-While-Sync-Partial", ""),
            new BPELTestCase().checkDeployment().sendSync(1, 1),
            new BPELTestCase().checkDeployment().sendSync(2, 2)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN_PARTIAL = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge-Partial",
            new Feature(WCP13, "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge-Partial", ""),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(100).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITH_A_PRIORI_DESGIN_TIME_KNOWLEDGE_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge",
            new Feature(WCP13, "WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge", ""),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(4)
    );

    public static
    final Test MULTIPLE_INSTANCES_WITH_A_PRIORI_RUNTIME_KNOWLEDGE_PATTERN = BPELProcessBuilder.buildPatternProcessWithPartner(
            "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledge",
            new Feature(WCP14, "WCP14-MultipleInstancesWithAPrioriRuntimeKnowledge", ""),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(1).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(2),
            new BPELTestCase().checkDeployment().buildPartnerConcurrencySetup().sendSync(2).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static final Test CANCEL_ACTIVITY_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP19-CancelActivity",
            new Feature(WCP19, "WCP19-CancelActivity", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1A"),
            new BPELTestCase().checkDeployment().sendSyncString(0, "0B")
    );

    public static final Test CANCEL_CASE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP20-CancelCase",
            new Feature(WCP20, "WCP20-CancelCase", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "1"),
            new BPELTestCase().checkDeployment().sendSyncString(0, new AssertExit())
    );

    public static final Test MILESTONE_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP18-Milestone",
            new Feature(WCP18, "WCP18-Milestone", ""),
            new BPELTestCase("PickAsyncMessage").checkDeployment().sendSync(1, 1).sendAsync(1).sendSyncString(1, "8"),
            new BPELTestCase("Pick3sTimeout").checkDeployment().sendSync(1, 1).waitFor(4_000).sendSyncString(1, "9")
    );
    public static final Test INTERLEAVED_PARALLEL_ROUTING_PATTERN = BPELProcessBuilder.buildPatternProcess(
            "WCP17-InterleavedParallelRouting",
            new Feature(WCP17, "WCP17-InterleavedParallelRouting", ""),
            new BPELTestCase().checkDeployment().sendSyncString(1, "AW1ABW2B")
    );

    public static final List<Test> CONTROL_FLOW_PATTERNS = Arrays.asList(
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
