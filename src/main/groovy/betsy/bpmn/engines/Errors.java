package betsy.bpmn.engines;

public interface Errors {

    String ERROR_RUNTIME = "ERROR_runtime";
    String ERROR_DEPLOYMENT = "ERROR_deployment";
    String ERROR_PROCESS_ABORTED = "ERROR_processAborted";

    String ERROR_THROWN_ERROR_EVENT = "ERROR_thrownErrorEvent";
    String ERROR_THROWN_ESCALATION_EVENT = "ERROR_thrownEscalationEvent";

}
