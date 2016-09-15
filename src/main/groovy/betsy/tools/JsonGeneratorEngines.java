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
import betsy.common.model.engine.EngineExtended;
import betsy.common.model.engine.IsEngine;
import org.json.JSONArray;
import org.json.JSONObject;

class JsonGeneratorEngines {

    public static void generateEnginesJson(Path folder) {
        JSONArray array = new JSONArray();

        for (EngineLifecycle e : getEngines()) {
            boolean excludeVirtualEngines = !(e instanceof VirtualEngineAPI);
            if (e instanceof IsEngine && excludeVirtualEngines) {
                EngineExtended engineExtended = ((IsEngine) e).getEngineObject();
                array.put(createEngineObject(engineExtended));
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(folder.resolve("engines.json"), StandardOpenOption.CREATE)) {
            writer.append(array.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject createEngineObject(EngineExtended engineExtended) {
        JSONObject object = new JSONObject();
        object.put("id", engineExtended.getNormalizedId());
        object.put("name", engineExtended.getName());
        object.put("version", engineExtended.getVersion());
        object.put("configuration", engineExtended.getConfiguration());
        object.put("language", engineExtended.getLanguage().name());
        object.put("programmingLanguage", engineExtended.getProgrammingLanguage());
        object.put("license", engineExtended.getLicense());
        object.put("licenseURL", engineExtended.getLicenseURL());
        object.put("releaseDate", DateTimeFormatter.ISO_LOCAL_DATE.format(engineExtended.getReleaseDate()));
        object.put("url", engineExtended.getURL());

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
