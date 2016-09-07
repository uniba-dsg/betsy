package betsy.bpmn.engines.activiti;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;

public class ActivitiLogBasedProcessInstanceOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    private final Path logFile;

    public ActivitiLogBasedProcessInstanceOutcomeChecker(Path logFile) {
        this.logFile = Objects.requireNonNull(logFile);
    }

    @Override
    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        List<String> lines = BPMNProcessInstanceOutcomeChecker.getLines(logFile);
        for (String line : lines) {
            if (line.contains("Ignoring unsupported activity type")) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            } else if (line.contains("org.activiti.engine.ActivitiException: Errors while parsing:")) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            } else if (line.contains("org.activiti.engine.ActivitiException")) {
                return ProcessInstanceOutcome.RUNTIME;
            } else if (line.contains("EndEvent_2 throws error event with errorCode 'ERR-1'")) {
                return ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            } else if (line.contains("No catching boundary event found for error with errorCode 'ERR-1'")) {
                return ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            }
        }

        return ProcessInstanceOutcome.UNKNOWN;
    }
}
