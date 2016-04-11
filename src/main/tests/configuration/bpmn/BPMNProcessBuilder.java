package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.Group;
import betsy.common.model.ProcessLanguage;
import betsy.common.util.FileTypes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class BPMNProcessBuilder {

    public static Group BASICS = new Group("basics", ProcessLanguage.BPMN, "The basic building blocks of BPMN.");
    public static Group ACTIVITIES = new Group("activities", ProcessLanguage.BPMN, "Activities can be Tasks, different kinds of SubProcesses, and CallActivities, and they model specific tasks in the real world.");
    public static Group GATEWAYS = new Group("gateways", ProcessLanguage.BPMN, "Gateways control the routing behavior of a process.");
    public static Group ERRORS = new Group("errors", ProcessLanguage.BPMN, "Faulty processes which should be detected upon deployment.");
    public static Group EVENTS = new Group("events", ProcessLanguage.BPMN, "Events make the BPMN process reactive, triggering start, end and intermediate events such as timers or messages.");
    public static Group DATA = new Group("data", ProcessLanguage.BPMN, "Model data flow within a process.");
    public static Group CFPATTERNS = new Group("cfpatterns", ProcessLanguage.BPMN, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.");

    public static BPMNProcess buildActivityProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("activities").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), ACTIVITIES);
    }

    public static BPMNProcess buildGatewayProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("gateways").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), GATEWAYS);
    }

    public static BPMNProcess buildErrorProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("errors").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), ERRORS);
    }

    public static BPMNProcess buildEventProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("events").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), EVENTS);
    }

    public static BPMNProcess buildBasicProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("basics").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), BASICS);
    }

    public static BPMNProcess buildDataProcess(String name, String description, BPMNTestCase... testCases) {
        return new BPMNProcess(ROOT_FOLDER.resolve("data").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), DATA);
    }
    public static BPMNProcess buildPatternProcess (String name, String description, BPMNTestCase... testCases){
        return new BPMNProcess(ROOT_FOLDER.resolve("cfpatterns").resolve(name + FileTypes.BPMN), description, Arrays.asList(testCases), CFPATTERNS);
    }
    public static final Path ROOT_FOLDER = Paths.get("src/main/tests/files/bpmn");
}
