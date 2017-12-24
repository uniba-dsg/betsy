package configuration.bpmn;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import betsy.bpmn.model.BPMNTestCaseBuilder;
import betsy.common.util.FileTypes;
import configuration.Capabilities;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.TestCase;

import static configuration.FilesLocation.BPMN_LOCATION;

public class BPMNProcessBuilder {

    public static final Path ROOT_FOLDER = Paths.get(BPMN_LOCATION);

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
        return Capabilities.addMetrics(new Test(ROOT_FOLDER.resolve("gateways").resolve(name + FileTypes.BPMN), description,
                convert(testCases, name),
                feature, Collections.emptyList()));
    }

    public static Test buildErrorProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return Capabilities.addMetrics(new Test(ROOT_FOLDER.resolve("errors").resolve(name + FileTypes.BPMN),
                description,
                convert(testCases, name),
                feature, Collections.emptyList()));
    }

    public static Test buildEventProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return Capabilities.addMetrics(new Test(ROOT_FOLDER.resolve("events").resolve(name + FileTypes.BPMN),
                description,
                convert(testCases, name),
                feature, Collections.emptyList()));
    }

    public static Test buildBasicProcess(String name, String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return Capabilities.addMetrics(new Test(ROOT_FOLDER.resolve("basics").resolve(name + FileTypes.BPMN), description,
                convert(testCases, name),
                feature, Collections.emptyList()));
    }

    public static Test buildDataProcess(String description, Feature feature, BPMNTestCaseBuilder... testCases) {
        return Capabilities.addMetrics(new Test(ROOT_FOLDER.resolve("data").resolve(feature.getName() + FileTypes.BPMN), description,
                convert(testCases, feature.getName()),
                feature, Collections.emptyList()));
    }

    public static Test buildPatternProcess (String description, Feature feature, BPMNTestCaseBuilder... testCases){
        return Capabilities.addMetrics(new Test(ROOT_FOLDER.resolve("cfpatterns").resolve(feature.getName() + FileTypes.BPMN), description,
                convert(testCases, feature.getName()),
                feature, Collections.emptyList()));
    }

    public static Test buildConstraintProcess (String description, Feature feature, BPMNTestCaseBuilder... testCases){
        return Capabilities.addMetrics(new Test(ROOT_FOLDER.resolve("cfpatterns").resolve(feature.getName() + FileTypes.BPMN), description,
                convert(testCases, feature.getName()),
                feature, Collections.emptyList()));
    }

}
