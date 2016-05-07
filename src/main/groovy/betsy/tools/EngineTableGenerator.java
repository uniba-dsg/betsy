package betsy.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.engine.Engine;
import betsy.common.model.engine.IsEngine;

public final class EngineTableGenerator {

    private EngineTableGenerator() {}

    public static void main(String[] args) {
        generateEnginesJson();
    }

    private static void generateEnginesJson() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("engines.tex"), StandardOpenOption.CREATE)) {
            writer.write("Language & Name & Version & License & Developed in & Released at & Configurations\\\\");
            writer.newLine();

            List<Engine> engines = getEngines().stream().filter(e -> !(e instanceof VirtualEngineAPI)).map(e -> ((IsEngine) e).getEngineObject()).collect(Collectors.toList());
            Collections.sort(engines, Comparator.comparing(Engine::getLanguage).thenComparing(Engine::getName).thenComparing(Engine::getVersion));
            for (Engine engine : engines) {
                String line = String.join(" & ", Arrays.asList(
                        engine.getLanguage().name(),
                        engine.getName(),
                        engine.getVersion(),
                        engine.getLicense(),
                        engine.getProgrammingLanguage(),
                        DateTimeFormatter.ISO_LOCAL_DATE.format(engine.getReleaseDate()),
                        String.join(", ", engine.getConfiguration()))) + "\\\\";

                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<EngineLifecycle> getEngines() {
        final List<EngineLifecycle> bpelEngines = new BPELEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> bpmnEngines = new BPMNEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> engines = new LinkedList<>();
        engines.addAll(bpelEngines);
        engines.addAll(bpmnEngines);

        return engines;
    }
}
