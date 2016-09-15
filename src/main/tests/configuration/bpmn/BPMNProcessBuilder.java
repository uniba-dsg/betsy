package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import pebl.test.Test;
import pebl.featuretree.Feature;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class BPMNProcessBuilder {

    public static final Path ROOT_FOLDER = Paths.get("src/main/tests/files/bpmn");

    public static Test buildActivityProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new Test(ROOT_FOLDER.resolve("activities").resolve(name + FileTypes.BPMN),
                description,
                Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static Test buildGatewayProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new Test(ROOT_FOLDER.resolve("gateways").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static Test buildErrorProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new Test(ROOT_FOLDER.resolve("errors").resolve(name + FileTypes.BPMN),
                description,
                Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static Test buildEventProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new Test(ROOT_FOLDER.resolve("events").resolve(name + FileTypes.BPMN),
                description,
                Arrays.asList(testCases),
                feature, Collections.emptyList());
    }

    public static Test buildBasicProcess(String name, String description, Feature feature, BPMNTestCase... testCases) {
        return new Test(ROOT_FOLDER.resolve("basics").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), feature, Collections.emptyList());
    }

    public static Test buildDataProcess(String description, Feature feature, BPMNTestCase... testCases) {
        return new Test(ROOT_FOLDER.resolve("data").resolve(feature.getName() + FileTypes.BPMN), description, Arrays.asList(testCases), feature, Collections.emptyList());
    }

    public static Test buildPatternProcess (String description, Feature feature, BPMNTestCase... testCases){
        return new Test(ROOT_FOLDER.resolve("cfpatterns").resolve(feature.getName() + FileTypes.BPMN), description, Arrays.asList(testCases), feature, Collections.emptyList());
    }

}
