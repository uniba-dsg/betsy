package configuration.bpmn;

import betsy.common.model.feature.Group;
import betsy.common.model.feature.Language;
import configuration.Capabilities;

public class Groups {

    public static Language CONFORMANCE_BPEL = new Language(Capabilities.conformance, "BPMN");
    public static Language EXPRESSIVENESS_BPEL = new Language(Capabilities.expressiveness, "BPMN");

    public static Group BASICS = new Group("basics", CONFORMANCE_BPEL, "The basic building blocks of BPMN.");
    public static Group ACTIVITIES = new Group("activities", CONFORMANCE_BPEL, "Activities can be Tasks, different kinds of SubProcesses, and CallActivities, and they model specific tasks in the real world.");
    public static Group GATEWAYS = new Group("gateways", CONFORMANCE_BPEL, "Gateways control the routing behavior of a process.");
    public static Group ERRORS = new Group("errors", CONFORMANCE_BPEL, "Faulty processes which should be detected upon deployment.");
    public static Group EVENTS = new Group("events", CONFORMANCE_BPEL, "Events make the BPMN process reactive, triggering start, end and intermediate events such as timers or messages.");
    public static Group DATA = new Group("data", CONFORMANCE_BPEL, "Model data flow within a process.");
    public static Group CFPATTERNS = new Group("cfpatterns", EXPRESSIVENESS_BPEL, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.");
}
