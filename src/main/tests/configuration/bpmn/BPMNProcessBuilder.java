package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.feature.Feature;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class BPMNProcessBuilder {

    public static final Path ROOT_FOLDER = Paths.get("src/main/tests/files/bpmn");

    public static EngineIndependentProcess buildActivityProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("activities").resolve(name + FileTypes.BPMN),
                description,
                Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static EngineIndependentProcess buildGatewayProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("gateways").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static EngineIndependentProcess buildErrorProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("errors").resolve(name + FileTypes.BPMN),
                description,
                Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static EngineIndependentProcess buildEventProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("events").resolve(name + FileTypes.BPMN),
                description,
                Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static EngineIndependentProcess buildBasicProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("basics").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), feature, Collections.emptyList());
    }

    public static EngineIndependentProcess buildDataProcess(String description, Feature feature, BPMNTestCase... testCases) {
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("data").resolve(feature.getName() + FileTypes.BPMN), description, Arrays.asList(testCases), feature, Collections.emptyList());
    }

    public static EngineIndependentProcess buildPatternProcess (String description, Feature feature, BPMNTestCase... testCases){
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("cfpatterns").resolve(feature.getName() + FileTypes.BPMN), description, Arrays.asList(testCases), feature, Collections.emptyList());
    }

}
