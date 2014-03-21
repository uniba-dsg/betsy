package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase
import betsy.data.engines.BPMNEngine

import java.nio.file.Path
import java.nio.file.Paths

class BPMNProcessBuilder {

    public static BPMNProcess buildTaskProcess(String name, String groupId, String version, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "tasks", groupId: groupId, version: version, description: description, testCases: testCases)
    }

    public static BPMNProcess buildGatewayProcess(String name, String groupId, String version, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "gateways", groupId: groupId, version: version, description: description, testCases: testCases)
    }

    public static BPMNProcess buildEventProcess(String name, String groupId, String version, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "events", groupId: groupId, version: version, description: description, testCases: testCases)
    }

    public static BPMNProcess buildSubprocessProcess(String name, String groupId, String version, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "subprocess", groupId: groupId, version: version, description: description, testCases: testCases)
    }

    public static BPMNProcess buildMiscProcess(String name, String groupId, String version, String description, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name, group: "misc", groupId: groupId, version: version, description: description, testCases: testCases)
    }
}