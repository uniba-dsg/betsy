package betsy.bpmn.engines.camunda;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessOutcomeChecker;

public class CamundaLogBasedProcessOutcomeChecker implements BPMNProcessOutcomeChecker {

    private final Path logFile;

    public CamundaLogBasedProcessOutcomeChecker(Path logFile) {
        this.logFile = Objects.requireNonNull(logFile);
    }

    @Override
    public ProcessOutcome checkProcessOutcome(String name) {
        List<String> lines = BPMNProcessOutcomeChecker.getLines(logFile);
        for (String line : lines) {
            if (line.contains("Ignoring unsupported activity type")) {
                return ProcessOutcome.UNDEPLOYED;
            } else if (line.contains("org.camunda.bpm.engine.ProcessEngineException: Unsupported")) {
                return ProcessOutcome.UNDEPLOYED;
            } else if (line.contains("org.camunda.bpm.engine.ProcessEngineException")) {
                return ProcessOutcome.RUNTIME;
            } else if (line.contains("EndEvent_2 throws error event with errorCode 'ERR-1'")) {
                return ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            } else if (line.contains("'EndEvent_2' throws an error event with errorCode 'ERR-1'")) {
                return ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            }
        }

        return ProcessOutcome.UNKNOWN;
    }

}
