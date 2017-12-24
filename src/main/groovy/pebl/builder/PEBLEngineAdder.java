package pebl.builder;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.engine.EngineExtended;
import betsy.common.model.engine.IsEngine;
import pebl.result.engine.Engine;
import pebl.xsd.PEBL;

public class PEBLEngineAdder {

    public static void addEngines(PEBL pebl) {
        pebl.result.engines.addAll(getEngines());
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
}
