package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNProcessBuilder {

    public static BPMNProcess buildActivityProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "activities", description: description, testCases: testCases)
    }

    public static BPMNProcess buildGatewayProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "gateways", description: description, testCases: testCases)
    }

    public static BPMNProcess buildErrorProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "errors", description: description, testCases: testCases)
    }

    public static BPMNProcess buildEventProcess(String name, String description, List<BPMNTestCase> testCases) {
        new BPMNProcess(name: name, group: "events", description: description, testCases: testCases)
    }

    public static BPMNProcess buildBasicProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "basics", description: description, testCases: testCases)
    }
}