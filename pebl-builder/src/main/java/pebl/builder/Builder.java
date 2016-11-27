package pebl.builder;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import configuration.Capabilities;
import configuration.MetricTypes;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.feature.Group;
import pebl.benchmark.feature.Language;
import pebl.benchmark.feature.MetricType;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.partner.NoTestPartner;
import pebl.xsd.Features;
import pebl.xsd.PEBL;

import static configuration.FilesLocation.BPMN_LOCATION;

public class Builder {

    public static final String EXTENSION_LANGUAGE_SUPPORT = "languageSupport";

    public static PEBL getPebl() {
        PEBL pebl = new PEBL();
        pebl.benchmark.tests.addAll(getTests().stream().collect(Collectors.toList()));
        pebl.benchmark.capabilities.addAll(new Features(getFeatures()).capabilities);
        pebl.benchmark.metricTypes.addAll(MetricTypes.getMetricTypes());

        addMetrics(pebl);
        addExtensionLanguageSupportForPatternImplementations(pebl);

        addPerformance(pebl);

        // as a very last step
        addNoTestPartnerForAllTestsWithoutTestPartners(pebl);

        return pebl;
    }

    private static void addNoTestPartnerForAllTestsWithoutTestPartners(PEBL pebl) {
        pebl.benchmark.tests.stream().filter(t -> t.getTestPartners().isEmpty()).forEach(t -> t.addTestPartner(new NoTestPartner()));
    }

    private static void addPerformance(PEBL pebl) {
        // feature tree
        final Capability performance = Capabilities.PERFORMANCE;
        final Language bpmn = new Language(performance, "BPMN")
                .addExtension("feature", "experiment")
                .addExtension("featureSet", "")
                .addExtension("group", "");
        final Group def = new Group("Default", bpmn, "");
        final FeatureSet microBenchmark = new FeatureSet(def, "Micro-Benchmark", "Micro-Benchmark of BPMN 2.0 Workflow Management Systems involving 7 Workflow Patterns");
        final Feature feature = new Feature(microBenchmark, "Micro-Benchmark", "Micro-Benchmark of BPMN 2.0 Workflow Management Systems involving 7 Workflow Patterns");
        pebl.benchmark.capabilities.add(performance);

        // test
        final Test test = new Test(
                Paths.get(BPMN_LOCATION).resolve("/basics/SequenceFlow.bpmn"),
                "One bpmn workflow containing 7 workflow control flow patterns.",
                Collections.emptyList(),
                feature
        );
        test.addExtension("loadFunction.description", "A load test function");
        test.addExtension("loadFunction.thinkTime", "1 sec");
        test.addExtension("loadFunction.rampUpTime", "30 sec");
        test.addExtension("loadFunction.steadyStateTime", "9:30 min");
        test.addExtension("loadFunction.rampDownTime", "0 sec");
        test.addExtension("loadFunction.connectionTimeout", "20 sec");
        test.addExtension("loadFunction.users.startUsers", "1");
        test.addExtension("loadFunction.users.steadyStateUsers", "1500");
        test.addExtension("loadFunction.users.endUsers", "1500");
        pebl.benchmark.tests.add(test);
    }

    private static void addExtensionLanguageSupportForPatternImplementations(PEBL pebl) {
        final Capability expressiveness = pebl.benchmark.capabilities.stream().filter(c -> c.getName().equals("Expressiveness")).findFirst().orElseThrow(() -> new IllegalStateException("Expressiveness Capability must be there"));
        addLanguageSupportForPatternImplementations(expressiveness);
        addLanguageSupportForPatterns(expressiveness);

        expressiveness.getLanguages()
                .stream()
                .flatMap(l -> l.getGroups().stream())
                .flatMap(g -> g.getFeatureSets().stream())
                .flatMap(fs -> fs.getFeatures().stream())
                .forEach(f -> f.addMetric(MetricTypes.PATTERN_IMPLEMENTATION_SUPPORT).addMetric(MetricTypes.PATTERN_IMPLEMENTATION_FULFILLED_LANGUAGE_SUPPORT));

        expressiveness.getLanguages()
                .stream()
                .flatMap(l -> l.getGroups().stream())
                .flatMap(g -> g.getFeatureSets().stream())
                .forEach(fs -> fs.addMetric(MetricTypes.PATTERN_SUPPORT).addMetric(MetricTypes.PATTERN_FULFILLED_LANGUAGE_SUPPORT));
    }

    private static void addLanguageSupportForPatternImplementations(Capability expressiveness) {
        final List<Feature> patternImplementations = expressiveness.getLanguages()
                .stream()
                .flatMap(l -> l.getGroups().stream())
                .flatMap(g -> g.getFeatureSets().stream())
                .flatMap(fs -> fs.getFeatures().stream())
                .collect(Collectors.toList());

        Map<String, String> patternImplToUpperBoundForBPMN = new HashMap<>();
        patternImplToUpperBoundForBPMN.put("WCP01_Sequence", "+");
        patternImplToUpperBoundForBPMN.put("WCP02_ParallelSplit", "+");
        patternImplToUpperBoundForBPMN.put("WCP03_Synchronization", "+");
        patternImplToUpperBoundForBPMN.put("WCP04_ExclusiveChoice", "+");
        patternImplToUpperBoundForBPMN.put("WCP05_SimpleMerge", "+");
        patternImplToUpperBoundForBPMN.put("WCP06_MultiChoice_InclusiveGateway", "+");
        patternImplToUpperBoundForBPMN.put("WCP06_MultiChoice_Implicit", "+");
        patternImplToUpperBoundForBPMN.put("WCP06_MultiChoice_ComplexGateway", "+");
        patternImplToUpperBoundForBPMN.put("WCP08_MultiMerge", "+");
        patternImplToUpperBoundForBPMN.put("WCP10_ArbitraryCycles", "+");
        patternImplToUpperBoundForBPMN.put("WCP11_ImplicitTermination", "+");
        patternImplToUpperBoundForBPMN.put("WCP12_MultipleInstancesWithoutSynchronization", "+");
        patternImplToUpperBoundForBPMN.put("WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge", "+");
        patternImplToUpperBoundForBPMN.put("WCP14_MultipleInstancesWithAPrioriRuntimeKnowledge", "+");
        patternImplToUpperBoundForBPMN.put("WCP16_DeferredChoice", "+");
        patternImplToUpperBoundForBPMN.put("WCP19_CancelTask", "+");
        patternImplToUpperBoundForBPMN.put("WCP20_CancelCaseError", "+");
        patternImplToUpperBoundForBPMN.put("WCP20_CancelCaseCancel", "+");
        patternImplToUpperBoundForBPMN.put("WCP20_CancelCaseTerminate", "+");
        patternImplToUpperBoundForBPMN.put("WCP07_StructuredSynchronizingMerge", "+/-");
        patternImplToUpperBoundForBPMN.put("WCP09_Structured_Discriminator_ComplexGateway", "+/-");
        patternImplToUpperBoundForBPMN.put("WCP09_Structured_Discriminator_MultiInstance", "+/-");
        patternImplToUpperBoundForBPMN.put("WCP17_InterleavedParallelRouting", "+/-");

        Map<String, String> patternImplToUpperBoundForBPEL = new HashMap<>();
        patternImplToUpperBoundForBPEL.put("WCP01-Sequence", "+");
        patternImplToUpperBoundForBPEL.put("WCP02-ParallelSplit", "+");
        patternImplToUpperBoundForBPEL.put("WCP03-Synchronization", "+");
        patternImplToUpperBoundForBPEL.put("WCP04-ExclusiveChoice", "+");
        patternImplToUpperBoundForBPEL.put("WCP05-SimpleMerge", "+");
        patternImplToUpperBoundForBPEL.put("WCP06-MultiChoice", "+");
        patternImplToUpperBoundForBPEL.put("WCP07-SynchronizingMerge", "+");
        patternImplToUpperBoundForBPEL.put("WCP11-ImplicitTermination", "+");
        patternImplToUpperBoundForBPEL.put("WCP16-DeferredChoice", "+");
        patternImplToUpperBoundForBPEL.put("WCP12-MultipleInstancesWithoutSynchronization", "+");
        patternImplToUpperBoundForBPEL.put("WCP12-MultipleInstancesWithoutSynchronization-Sync", "+");
        patternImplToUpperBoundForBPEL.put("WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge", "+");
        patternImplToUpperBoundForBPEL.put("WCP14-MultipleInstancesWithAPrioriRuntimeKnowledge", "+");
        patternImplToUpperBoundForBPEL.put("WCP20-CancelCase", "+");
        patternImplToUpperBoundForBPEL.put("WCP06-MultiChoice-Partial", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP07-SynchronizingMerge-Partial", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP12-MultipleInstancesWithoutSynchronization-Partial", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP12-MultipleInstancesWithoutSynchronization-Sync-Partial", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP12-MultipleInstancesWithoutSynchronization-While-Partial", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP12-MultipleInstancesWithoutSynchronization-While-Sync-Partial", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP13-MultipleInstancesWithAPrioriDesignTimeKnowledge-Partial", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP19-CancelActivity", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP18-Milestone", "+/-");
        patternImplToUpperBoundForBPEL.put("WCP17-InterleavedParallelRouting", "+/-");

        for (Feature feature : patternImplementations) {
            final String name = feature.getName();
            if (patternImplToUpperBoundForBPEL.containsKey(name)) {
                feature.addExtension(EXTENSION_LANGUAGE_SUPPORT, patternImplToUpperBoundForBPEL.get(name));
            } else if (patternImplToUpperBoundForBPMN.containsKey(name)) {
                feature.addExtension(EXTENSION_LANGUAGE_SUPPORT, patternImplToUpperBoundForBPMN.get(name));
            } else {
                throw new IllegalStateException("should not happen for feature " + name);
            }
        }
    }

    private static void addLanguageSupportForPatterns(Capability expressiveness) {
        final List<FeatureSet> patterns = expressiveness.getLanguages()
                .stream()
                .flatMap(l -> l.getGroups().stream())
                .flatMap(g -> g.getFeatureSets().stream())
                .collect(Collectors.toList());

        Map<String, String> patternToUpperBoundForBPMN = new HashMap<>();
        patternToUpperBoundForBPMN.put("WCP01_Sequence", "+");
        patternToUpperBoundForBPMN.put("WCP02_ParallelSplit", "+");
        patternToUpperBoundForBPMN.put("WCP03_Synchronization", "+");
        patternToUpperBoundForBPMN.put("WCP04_ExclusiveChoice", "+");
        patternToUpperBoundForBPMN.put("WCP05_SimpleMerge", "+");
        patternToUpperBoundForBPMN.put("WCP06_MultiChoice", "+");
        patternToUpperBoundForBPMN.put("WCP08_MultiMerge", "+");
        patternToUpperBoundForBPMN.put("WCP10_ArbitraryCycles", "+");
        patternToUpperBoundForBPMN.put("WCP11_ImplicitTermination", "+");
        patternToUpperBoundForBPMN.put("WCP12_MultipleInstancesWithoutSynchronization", "+");
        patternToUpperBoundForBPMN.put("WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge", "+");
        patternToUpperBoundForBPMN.put("WCP14_MultipleInstancesWithAPrioriRuntimeKnowledge", "+");
        patternToUpperBoundForBPMN.put("WCP16_DeferredChoice", "+");
        patternToUpperBoundForBPMN.put("WCP19_CancelTask", "+");
        patternToUpperBoundForBPMN.put("WCP20_CancelCase", "+");
        patternToUpperBoundForBPMN.put("WCP07_StructuredSynchronizingMerge", "+/-");
        patternToUpperBoundForBPMN.put("WCP09_Structured_Discriminator", "+/-");
        patternToUpperBoundForBPMN.put("WCP17_InterleavedParallelRouting", "+/-");

        Map<String, String> patternToUpperBoundForBPEL = new HashMap<>();
        patternToUpperBoundForBPEL.put("WCP01_Sequence", "+");
        patternToUpperBoundForBPEL.put("WCP02_ParallelSplit", "+");
        patternToUpperBoundForBPEL.put("WCP03_Synchronization", "+");
        patternToUpperBoundForBPEL.put("WCP04_ExclusiveChoice", "+");
        patternToUpperBoundForBPEL.put("WCP05_SimpleMerge", "+");
        patternToUpperBoundForBPEL.put("WCP06_MultiChoice", "+");
        patternToUpperBoundForBPEL.put("WCP07_SynchronizingMerge", "+");
        patternToUpperBoundForBPEL.put("WCP11_ImplicitTermination", "+");
        patternToUpperBoundForBPEL.put("WCP16_DeferredChoice", "+");
        patternToUpperBoundForBPEL.put("WCP12_MultipleInstancesWithoutSynchronization", "+");
        patternToUpperBoundForBPEL.put("WCP13_MultipleInstancesWithAPrioriDesignTimeKnowledge", "+");
        patternToUpperBoundForBPEL.put("WCP14_MultipleInstancesWithAPrioriRuntimeKnowledge", "+");
        patternToUpperBoundForBPEL.put("WCP20_CancelCase", "+");
        patternToUpperBoundForBPEL.put("WCP19_CancelActivity", "+/-");
        patternToUpperBoundForBPEL.put("WCP18_Milestone", "+/-");
        patternToUpperBoundForBPEL.put("WCP17_InterleavedParallelRouting", "+/-");

        for (FeatureSet featureSet : patterns) {
            final String name = featureSet.getName();
            if (patternToUpperBoundForBPEL.containsKey(name)) {
                featureSet.addExtension(EXTENSION_LANGUAGE_SUPPORT, patternToUpperBoundForBPEL.get(name));
            } else if (patternToUpperBoundForBPMN.containsKey(name)) {
                featureSet.addExtension(EXTENSION_LANGUAGE_SUPPORT, patternToUpperBoundForBPMN.get(name));
            } else {
                throw new IllegalStateException("should not happen for feature set " + featureSet.getId());
            }
        }
    }

    private static void addMetrics(PEBL pebl) {
        List<MetricType> metricTypes = Arrays.asList(
                MetricTypes.TEST_CASES_SUM,
                MetricTypes.TEST_CASES_SUCCESSFUL_SUM,
                MetricTypes.TEST_CASES_FAILURE_SUM,
                MetricTypes.TEST_DEPLOYABLE_COUNT,
                MetricTypes.TEST_SUCCESSFUL_COUNT,
                MetricTypes.TEST_RESULT_TRIVALENT_AGGREGATION,
                MetricTypes.TESTS_COUNT
        );

        // apply metrics
        pebl.benchmark.capabilities.stream().filter(c -> !c.getName().equals("Performance")).forEach(c -> {

            for (MetricType metricType : metricTypes) {
                c.addMetric(metricType);
            }

            c.getLanguages().forEach(l -> {

                for (MetricType metricType : metricTypes) {
                    l.addMetric(metricType);
                }

                l.getGroups().forEach(g -> {

                    for (MetricType metricType : metricTypes) {
                        g.addMetric(metricType);
                    }

                    g.getFeatureSets().forEach(fs -> {

                        for (MetricType metricType : metricTypes) {
                            fs.addMetric(metricType);
                        }

                        fs.getFeatures().forEach(f -> {

                            for (MetricType metricType : metricTypes) {
                                f.addMetric(metricType);
                            }

                        });
                    });
                });
            });
        });
    }

    public static List<Feature> getFeatures() {
        return getTests().stream().map(Test::getFeature).distinct().collect(Collectors.toList());
    }

    public static List<Test> getTests() {
        List<Test> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ERRORS"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("STATIC_ANALYSIS"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        return processes;
    }
}
