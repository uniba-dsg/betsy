package betsy.bpmn.engines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import betsy.bpmn.model.BPMNAssertions;
import com.google.common.base.Charsets;

public interface BPMNProcessOutcomeChecker {

    static List<String> getLines(Path file) {
        try {
            return Files.readAllLines(file, Charsets.ISO_8859_1);
        } catch (IOException e) {
            try {
                return Files.readAllLines(file, Charsets.UTF_8);
            } catch (IOException e1) {
                throw new RuntimeException("could not read file with either ISO 8859 1 or UTF 8" + file, e1);
            }
        }
    }

    enum ProcessOutcome {
        PROCESS_ABORTED,
        PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN,
        PROCESS_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN,
        COULD_NOT_CHECK_PROCESS_STATUS,
        OK,
        UNKNOWN,
        UNDEPLOYED,
        RUNTIME
    }

    /**
     * Is applied on the most recent instance of the process
     *
     * @param name the name of the process
     * @return
     */
    public ProcessOutcome checkProcessOutcome(String name);

}
