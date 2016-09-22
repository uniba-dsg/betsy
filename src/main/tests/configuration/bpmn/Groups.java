package configuration.bpmn;

import pebl.feature.Group;
import pebl.feature.Language;
import configuration.Capabilities;

public class Groups {

    public static Language CONFORMANCE_BPMN = new Language(Capabilities.CONFORMANCE, "BPMN");
    public static Language EXPRESSIVENESS_BPMN = new Language(Capabilities.EXPRESSIVENESS, "BPMN");

    public static Group BASICS = new Group("basics", CONFORMANCE_BPMN, "The basic building blocks of BPMN.");
    public static Group ACTIVITIES = new Group("activities", CONFORMANCE_BPMN, "Activities can be Tasks, different kinds of SubProcesses, and CallActivities, and they model specific tasks in the real world.");
    public static Group GATEWAYS = new Group("gateways", CONFORMANCE_BPMN, "Gateways control the routing behavior of a process.");
    public static Group ERRORS = new Group("errors", CONFORMANCE_BPMN, "Faulty processes which should be detected upon deployment.");
    public static Group EVENTS = new Group("events", CONFORMANCE_BPMN, "Events make the BPMN process reactive, triggering start, end and intermediate events such as timers or messages.");
    public static Group DATA = new Group("data", CONFORMANCE_BPMN, "Model data flow within a process.");
    public static Group BPMN_CONSTRAINTS = new Group("bpmn_constraints", CONFORMANCE_BPMN, "Processes violating constraints stated in the specification.");
    public static Group CFPATTERNS = new Group("cfpatterns", EXPRESSIVENESS_BPMN, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.");

}
