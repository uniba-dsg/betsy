package betsy.bpmn.engines.jbpm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessOutcomeChecker;
import com.google.common.base.Charsets;

public class JbpmLogBasedProcessOutcomeChecker implements BPMNProcessOutcomeChecker {

    private final Path logFile;

    public JbpmLogBasedProcessOutcomeChecker(Path logFile) {
        this.logFile = Objects.requireNonNull(logFile);
    }

    @Override
    public ProcessOutcome checkProcessOutcome(String name) {
        List<String> lines = getLines();
        for (String line : lines) {
            if (line.contains("failed to deploy")) {
                return ProcessOutcome.UNDEPLOYED;
            } else if (line.contains("Unable to deploy")) {
                return ProcessOutcome.UNDEPLOYED;
            } else if (line.contains("ProcessLoadError")) {
                return ProcessOutcome.UNDEPLOYED;
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
