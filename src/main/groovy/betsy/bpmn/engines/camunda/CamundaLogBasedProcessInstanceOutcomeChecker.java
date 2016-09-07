package betsy.bpmn.engines.camunda;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;

public class CamundaLogBasedProcessInstanceOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    private final Path logFile;

    public CamundaLogBasedProcessInstanceOutcomeChecker(Path logFile) {
        this.logFile = Objects.requireNonNull(logFile);
    }

    @Override
    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        List<String> lines = BPMNProcessInstanceOutcomeChecker.getLines(logFile);
        for (String line : lines) {
            if (line.contains("Ignoring unsupported activity type")) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            } else if (line.contains("org.camunda.bpm.engine.ProcessEngineException: Unsupported")) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            } else if (line.contains("org.camunda.bpm.engine.ProcessEngineException")) {
                return ProcessInstanceOutcome.RUNTIME;
            } else if (line.contains("EndEvent_2 throws error event with errorCode 'ERR-1'")) {
                return ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            } else if (line.contains("'EndEvent_2' throws an error event with errorCode 'ERR-1'")) {
                return ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            }
        }

        return ProcessInstanceOutcome.UNKNOWN;
    }

}
