package betsy.tools;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import pebl.benchmark.feature.Feature;
import pebl.benchmark.test.Test;
import pebl.result.engine.Engine;
import pebl.result.tool.Tool;
import pebl.xsd.Features;
import pebl.xsd.PEBL;

public class PEBLBuilder {

    public static PEBL getPebl() {
        PEBL pebl = new PEBL();
        pebl.result.tools.addAll(getTools());
        pebl.result.engines.addAll(getEngines());
        pebl.benchmark.tests.addAll(getTests().stream().collect(Collectors.toList()));
        pebl.benchmark.capabilities.addAll(new Features(getFeatures()).capabilities);
        pebl.benchmark.metricTypes.addAll(MetricTypes.getMetricTypes());
        return pebl;
    }

    static List<Feature> getFeatures() {
        return getTests().stream().map(Test::getFeature).distinct().collect(Collectors.toList());
    }

    static List<Engine> getEngines() {
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

    static List<Test> getTests() {
        List<Test> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ERRORS"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("STATIC_ANALYSIS"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        return processes;
    }

    static List<Tool> getTools() {
        return Collections.singletonList(new Tool("betsy", GitUtil.getGitCommit()));
    }
}
