package betsy.tools.pebl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.engine.EngineExtended;
import betsy.common.model.engine.IsEngine;
import betsy.common.util.GitUtil;
import configuration.MetricTypes;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.Language;
import pebl.benchmark.feature.ScriptMetricType;
import pebl.benchmark.test.Test;
import pebl.result.engine.Engine;
import pebl.result.tool.Tool;
import pebl.xsd.Features;
import pebl.xsd.PEBL;

public class PEBLBuilder {

    public static final String EXTENSION_LANGUAGE_SUPPORT = "languageSupport";

    public static PEBL getPebl() {
        PEBL pebl = new PEBL();
        pebl.result.tools.addAll(getTools());
        pebl.result.engines.addAll(getEngines());
        pebl.benchmark.tests.addAll(getTests().stream().collect(Collectors.toList()));
        pebl.benchmark.capabilities.addAll(new Features(getFeatures()).capabilities);
        pebl.benchmark.metricTypes.addAll(MetricTypes.getMetricTypes());

        addMetrics(pebl);
        addExtensionLanguageSupportForPatternImplementations(pebl);

        return pebl;
    }

    private static void addExtensionLanguageSupportForPatternImplementations(PEBL pebl) {
        final Capability expressiveness = pebl.benchmark.capabilities.stream().filter(c -> c.getName().equals("Expressiveness")).findFirst().orElseThrow(() -> new IllegalStateException("Expressiveness Capability must be there"));
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

        expressiveness.getLanguages()
                .stream()
                .flatMap(l -> l.getGroups().stream())
                .flatMap(g -> g.getFeatureSets().stream())
                .flatMap(fs -> fs.getFeatures().stream())
                .forEach(f -> f.addMetric(MetricTypes.PATTERN_SUPPORT));

        expressiveness.getLanguages()
                .stream()
                .flatMap(l -> l.getGroups().stream())
                .flatMap(g -> g.getFeatureSets().stream())
                .forEach(fs -> fs.addMetric(MetricTypes.PATTERN_SUPPORT));
    }

    private static void addMetrics(PEBL pebl) {
        List<ScriptMetricType> metricTypes = Arrays.asList(
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

            for (ScriptMetricType metricType : metricTypes) {
                c.addMetric(metricType);
            }

            c.getLanguages().forEach(l -> {

                for (ScriptMetricType metricType : metricTypes) {
                    l.addMetric(metricType);
                }

                l.getGroups().forEach(g -> {

                    for (ScriptMetricType metricType : metricTypes) {
                        g.addMetric(metricType);
                    }

                    g.getFeatureSets().forEach(fs -> {

                        for (ScriptMetricType metricType : metricTypes) {
                            fs.addMetric(metricType);
                        }

                        fs.getFeatures().forEach(f -> {

                            for (ScriptMetricType metricType : metricTypes) {
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

    public static List<Engine> getEngines() {
        return getEngineLifecycles().stream()
                .filter(e -> !(e instanceof VirtualEngineAPI))
                .map(e -> (IsEngine) e)
                .map(IsEngine::getEngineObject)
                .map(EngineExtended::getEngine)
                .collect(Collectors.toList());
    }

    private static List<EngineLifecycle> getEngineLifecycles() {
        final List<EngineLifecycle> bpelEngines = new BPELEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> bpmnEngines = new BPMNEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> engines = new LinkedList<>();
        engines.addAll(bpelEngines);
        engines.addAll(bpmnEngines);

        return engines;
    }

    public static List<Test> getTests() {
        List<Test> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ERRORS"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("STATIC_ANALYSIS"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        return processes;
    }

    public static List<Tool> getTools() {
        return Collections.singletonList(new Tool("betsy", GitUtil.getGitCommit()));
    }
}
