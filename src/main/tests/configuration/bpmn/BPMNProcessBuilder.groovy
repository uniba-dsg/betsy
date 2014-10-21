package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNProcessBuilder {

    public static BPMNProcess buildTaskProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "tasks", description: description, testCases: testCases)
    }

    public static BPMNProcess buildGatewayProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "gateways", description: description, testCases: testCases)
    }

    public static BPMNProcess buildErrorProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "errors", description: description, testCases: testCases)
    }

    public static BPMNProcess buildEventProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "events", description: description, testCases: testCases)
    }

    public static BPMNProcess buildProcessWithSubProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "subprocess", description: description, testCases: testCases)
    }

    public static BPMNProcess buildMiscProcess(String name, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "misc", description: description, testCases: testCases)
    }
}