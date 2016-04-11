package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.EngineIndependentProcess;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class BPMNProcessBuilder {

    public static BPMNProcess buildActivityProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("activities").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), Groups.ACTIVITIES);
    }

    public static BPMNProcess buildGatewayProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("gateways").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), Groups.GATEWAYS);
    }

    public static BPMNProcess buildErrorProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("errors").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), Groups.ERRORS);
    }

    public static BPMNProcess buildEventProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("events").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), Groups.EVENTS);
    }

    public static BPMNProcess buildBasicProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("basics").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), Groups.BASICS);
    }

    public static EngineIndependentProcess buildDataProcess(String description, Feature feature, BPMNTestCase... testCases) {
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("data").resolve(feature.getName() + FileTypes.BPMN), description, Arrays.asList(testCases), feature);
    }

    public static EngineIndependentProcess buildPatternProcess (String description, Feature feature, BPMNTestCase... testCases){
        return new EngineIndependentProcess(ROOT_FOLDER.resolve("cfpatterns").resolve(feature.getName() + FileTypes.BPMN), description, Arrays.asList(testCases), feature);
    }
    public static final Path ROOT_FOLDER = Paths.get("src/main/tests/files/bpmn");
}
