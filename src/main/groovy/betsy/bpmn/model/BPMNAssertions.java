package betsy.bpmn.model;

public enum BPMNAssertions {

    SCRIPT_task1("SCRIPT_task1"),
    SCRIPT_task2("SCRIPT_task2"),
    SCRIPT_task3("SCRIPT_task3"),
    SCRIPT_task4("SCRIPT_task4"),
    SCRIPT_task5("SCRIPT_task5"),

    ERROR_RUNTIME("ERROR_runtime"),
    ERROR_DEPLOYMENT("ERROR_deployment"),
    ERROR_PROCESS_ABORTED("ERROR_processAborted"),
    ERROR_THROWN_ERROR_EVENT("ERROR_thrownErrorEvent"),
    ERROR_THROWN_ESCALATION_EVENT("ERROR_thrownEscalationEvent");

    private final String name;

    private BPMNAssertions(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
