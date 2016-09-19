package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCaseBuilder;
import pebl.test.Test;
import pebl.feature.Feature;
import betsy.common.util.FileTypes;
import pebl.test.TestCase;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BPMNProcessBuilder {

    public static final Path ROOT_FOLDER = Paths.get("src/main/tests/files/bpmn");

    public static Test buildActivityProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return new Test(ROOT_FOLDER.resolve("activities").resolve(name + FileTypes.BPMN),
                description,
                convert(testCases, name),
                feature, Collections.emptyList());
    }

    private static List<TestCase> convert(BPMNTestCaseBuilder[] testCases, String name) {
        List<TestCase> testCaseList = new LinkedList<>();
        for(int i = 1; i <= testCases.length; i++) {
            testCaseList.add(testCases[i - 1].getTestCase(i, name));
        }
        return testCaseList;
    }

    public static Test buildGatewayProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return new Test(ROOT_FOLDER.resolve("gateways").resolve(name + FileTypes.BPMN), description,
                convert(testCases, name),
                feature, Collections.emptyList());
    }

    public static Test buildErrorProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return new Test(ROOT_FOLDER.resolve("errors").resolve(name + FileTypes.BPMN),
                description,
                convert(testCases, name),
                feature, Collections.emptyList());
    }

    public static Test buildEventProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return new Test(ROOT_FOLDER.resolve("events").resolve(name + FileTypes.BPMN),
                description,
                convert(testCases, name),
                feature, Collections.emptyList());
    }

    public static Test buildBasicProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return new Test(ROOT_FOLDER.resolve("basics").resolve(name + FileTypes.BPMN), description,
                convert(testCases, name),
                feature, Collections.emptyList());
    }

    public static Test buildDataProcess(String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return new Test(ROOT_FOLDER.resolve("data").resolve(feature.getName() + FileTypes.BPMN), description,
                convert(testCases, feature.getName()),
                feature, Collections.emptyList());
    }

    public static Test buildPatternProcess (String description, Feature feature, BPMNTestCaseBuilder... testCases){
        return new Test(ROOT_FOLDER.resolve("cfpatterns").resolve(feature.getName() + FileTypes.BPMN), description,
                convert(testCases, feature.getName()),
                feature, Collections.emptyList());
    }

}
