package betsy.bpmn.engines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Charsets;

public interface BPMNProcessInstanceOutcomeChecker {

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

    enum ProcessInstanceOutcome {
        PROCESS_INSTANCE_ABORTED,
        PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN,
        PROCESS_INSTANCE_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN,
        COULD_NOT_CHECK_PROCESS_INSTANCE_STATUS,
        OK,
        UNKNOWN,
        UNDEPLOYED_PROCESS,
        RUNTIME
    }

    /**
     * Is applied on the most recent instance of the process
     *
     * @param name the name of the process
     * @return
     */
    public ProcessInstanceOutcome checkProcessOutcome(String name);

}
