package betsy.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import betsy.common.model.feature.Capability;
import betsy.common.model.feature.FeatureSet;
import betsy.common.model.feature.Feature;
import betsy.common.model.feature.FeatureDimension;
import betsy.common.model.feature.Group;
import betsy.common.model.feature.Language;
import betsy.common.model.input.EngineIndependentProcess;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import org.json.JSONArray;
import org.json.JSONObject;

class JsonGeneratorFeatureTree {

    public static void generatesConstructsJson(Path folder) {
        JSONArray featureTree = new JSONArray();

        List<EngineIndependentProcess> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        convertProcess(featureTree, processes);

        try(BufferedWriter writer = Files.newBufferedWriter(folder.resolve("feature-tree.json"), StandardOpenOption.CREATE)) {
            writer.append(featureTree.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generatesConstructsJsonWithReallyAllProcesses(Path folder) {
        JSONArray featureTree = new JSONArray();

        List<EngineIndependentProcess> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ERRORS"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("STATIC_ANALYSIS"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        convertProcess(featureTree, processes);

        try(BufferedWriter writer = Files.newBufferedWriter(folder.resolve("feature-tree.json"), StandardOpenOption.CREATE)) {
            writer.append(featureTree.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertProcess(JSONArray rootArray, List<EngineIndependentProcess> processes) {
        Map<Capability, Map<Language, Map<Group, Map<FeatureSet, List<EngineIndependentProcess>>>>> entries;
        entries = processes.stream().
                collect(Collectors.groupingBy(FeatureDimension::getCapability,
                        Collectors.groupingBy(FeatureDimension::getLanguage,
                        Collectors.groupingBy(FeatureDimension::getGroup,
                        Collectors.groupingBy(FeatureDimension::getFeatureSet)))));

        JSONArray capabilityArray = rootArray;

        for(Map.Entry<Capability, Map<Language, Map<Group, Map<FeatureSet, List<EngineIndependentProcess>>>>> entryCapability : entries.entrySet()) {
            Capability capability = entryCapability.getKey();
            JSONObject capabilityObject = new JSONObject();
            capabilityObject.put("name", capability.getName());
            capabilityObject.put("id", capability.getID());
            JSONArray languagesArray = new JSONArray();
            capabilityObject.put("languages", languagesArray);

            for(Map.Entry<Language, Map<Group, Map<FeatureSet, List<EngineIndependentProcess>>>> entryLanguage : entryCapability.getValue().entrySet()) {
                Language language = entryLanguage.getKey();
                JSONObject languageObject = new JSONObject();
                languageObject.put("name", language.getName());
                languageObject.put("id", language.getID());
                JSONArray groupsArray = new JSONArray();
                languageObject.put("groups", groupsArray);

                for(Map.Entry<Group, Map<FeatureSet, List<EngineIndependentProcess>>> entryGroup : entryLanguage.getValue().entrySet()) {
                    Group group = entryGroup.getKey();

                    JSONObject groupObject = new JSONObject();
                    groupObject.put("name", group.getName());
                    groupObject.put("description", group.description);
                    groupObject.put("id", group.getID());
                    JSONArray constructsArray = new JSONArray();
                    groupObject.put("constructs", constructsArray);

                    for(Map.Entry<FeatureSet, List<EngineIndependentProcess>> entryConstruct : entryGroup.getValue().entrySet()) {
                        FeatureSet construct = entryConstruct.getKey();

                        JSONObject constructObject = new JSONObject();
                        constructObject.put("name", construct.getName());
                        constructObject.put("id", construct.getID());
                        constructObject.put("description", construct.description);
                        JSONArray featuresArray = new JSONArray();
                        constructObject.put("features", featuresArray);

                        for(EngineIndependentProcess process : entryConstruct.getValue()) {
                            groupObject.put("description", process.getGroup().description);

                            featuresArray.put(createFeatureObject(process));
                        }
                        constructsArray.put(constructObject);
                    }
                    groupsArray.put(groupObject);
                }
                languagesArray.put(languageObject);
            }
            capabilityArray.put(capabilityObject);
        }
    }

    private static JSONObject createFeatureObject(EngineIndependentProcess process) {
        Feature feature = process.getFeature();
        JSONObject featureObject = new JSONObject();
        featureObject.put("id", feature.getID());
        featureObject.put("name", feature.getName());
        featureObject.put("description", feature.description);
        feature.upperBound.ifPresent(upperBound -> featureObject.put("upperBound", upperBound));
        return featureObject;
    }
}
