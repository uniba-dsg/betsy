package betsy.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.engine.Engine;
import betsy.common.model.engine.IsEngine;
import org.json.JSONArray;
import org.json.JSONObject;

class JsonGeneratorEngines {

    public static void generateEnginesJson(Path folder) {
        JSONArray array = new JSONArray();

        for (EngineLifecycle e : getEngines()) {
            boolean excludeVirtualEngines = !(e instanceof VirtualEngineAPI);
            if (e instanceof IsEngine && excludeVirtualEngines) {
                Engine engine = ((IsEngine) e).getEngineObject();
                array.put(createEngineObject(engine));
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(folder.resolve("engines.json"), StandardOpenOption.CREATE)) {
            writer.append(array.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject createEngineObject(Engine engine) {
        JSONObject object = new JSONObject();
        object.put("id", engine.getNormalizedId());
        object.put("name", engine.getName());
        object.put("version", engine.getVersion());
        object.put("configuration", engine.getConfiguration());
        object.put("language", engine.getLanguage().name());
        object.put("programmingLanguage", engine.getProgrammingLanguage());
        object.put("license", engine.getLicense());
        object.put("licenseURL", engine.getLicenseURL());
        object.put("releaseDate", DateTimeFormatter.ISO_LOCAL_DATE.format(engine.getReleaseDate()));
        object.put("url", engine.getURL());

        return object;
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
