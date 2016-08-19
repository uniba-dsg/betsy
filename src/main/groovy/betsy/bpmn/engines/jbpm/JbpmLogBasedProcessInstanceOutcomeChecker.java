package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;

public class JbpmLogBasedProcessInstanceOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    private final Path logFile;

    public JbpmLogBasedProcessInstanceOutcomeChecker(Path logFile) {
        this.logFile = Objects.requireNonNull(logFile);
    }

    @Override
    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        List<String> lines = BPMNProcessInstanceOutcomeChecker.getLines(logFile);
        for (String line : lines) {
            if (line.contains("failed to deploy")) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            } else if (line.contains("Unable to deploy")) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            } else if (line.contains("ProcessLoadError")) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            }
        }

        return ProcessInstanceOutcome.UNKNOWN;
    }

}
