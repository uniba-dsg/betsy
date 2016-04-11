package configuration.bpmn;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.feature.Group;

public class Groups {
    public static Group BASICS = new Group("basics", ProcessLanguage.BPMN, "The basic building blocks of BPMN.");
    public static Group ACTIVITIES = new Group("activities", ProcessLanguage.BPMN, "Activities can be Tasks, different kinds of SubProcesses, and CallActivities, and they model specific tasks in the real world.");
    public static Group GATEWAYS = new Group("gateways", ProcessLanguage.BPMN, "Gateways control the routing behavior of a process.");
    public static Group ERRORS = new Group("errors", ProcessLanguage.BPMN, "Faulty processes which should be detected upon deployment.");
    public static Group EVENTS = new Group("events", ProcessLanguage.BPMN, "Events make the BPMN process reactive, triggering start, end and intermediate events such as timers or messages.");
    public static Group DATA = new Group("data", ProcessLanguage.BPMN, "Model data flow within a process.");
    public static Group CFPATTERNS = new Group("cfpatterns", ProcessLanguage.BPMN, "The original 20 Workflow Control-Flow patterns from van der Aalst et al.");
}
