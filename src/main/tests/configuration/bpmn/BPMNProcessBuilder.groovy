package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

import java.nio.file.Path
import java.nio.file.Paths

class BPMNProcessBuilder {

    public static final Path ROOT_FOLDER = Paths.get("src/main/tests/files/bpmn")

    public static BPMNProcess buildActivityProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(process: ROOT_FOLDER.resolve("activities").resolve(name + ".bpmn"), description: description, testCases: testCases)
    }

    public static BPMNProcess buildGatewayProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(process: ROOT_FOLDER.resolve("gateways").resolve(name + ".bpmn"), description: description, testCases: testCases)
    }

    public static BPMNProcess buildErrorProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(process: ROOT_FOLDER.resolve("errors").resolve(name + ".bpmn"), description: description, testCases: testCases)
    }

    public static BPMNProcess buildEventProcess(String name, String description, List<BPMNTestCase> testCases) {
        new BPMNProcess(process: ROOT_FOLDER.resolve("events").resolve(name + ".bpmn"), description: description, testCases: testCases)
    }

    public static BPMNProcess buildBasicProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(process: ROOT_FOLDER.resolve("basics").resolve(name + ".bpmn"), description: description, testCases: testCases)
    }
}
