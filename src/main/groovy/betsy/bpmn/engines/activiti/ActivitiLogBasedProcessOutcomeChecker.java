package betsy.bpmn.engines.activiti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessOutcomeChecker;
import com.google.common.base.Charsets;

public class ActivitiLogBasedProcessOutcomeChecker implements BPMNProcessOutcomeChecker {

    private final Path logFile;

    public ActivitiLogBasedProcessOutcomeChecker(Path logFile) {
        this.logFile = Objects.requireNonNull(logFile);
    }

    @Override
    public ProcessOutcome checkProcessOutcome(String name) {
        List<String> lines = getLines();
        for (String line : lines) {
            if (line.contains("Ignoring unsupported activity type")) {
                return ProcessOutcome.UNDEPLOYED;
            } else if (line.contains("org.activiti.engine.ActivitiException: Errors while parsing:")) {
                return ProcessOutcome.UNDEPLOYED;
            } else if (line.contains("org.activiti.engine.ActivitiException")) {
                return ProcessOutcome.RUNTIME;
            } else if (line.contains("EndEvent_2 throws error event with errorCode 'ERR-1'")) {
                return ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            } else if (line.contains("No catching boundary event found for error with errorCode 'ERR-1'")) {
                return ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            }
        }

        return ProcessOutcome.UNKNOWN;
    }

    private List<String> getLines() {
        try {
            return Files.readAllLines(logFile, Charsets.ISO_8859_1);
        } catch (IOException e) {
            try {
                return Files.readAllLines(logFile, Charsets.UTF_8);
            } catch (IOException e1) {
                throw new RuntimeException("could not read file with either ISO 8859 1 or UTF 8" + logFile, e1);
            }
        }
    }
}
