package betsy.bpmn.engines;

import java.util.Optional;

import betsy.bpmn.model.BPMNAssertions;

public interface BPMNProcessOutcomeChecker {

    enum ProcessOutcome {
        PROCESS_ABORTED, PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN, PROCESS_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN, COULD_NOT_CHECK_PROCESS_STATUS, OK, UNKNOWN
    }

    public ProcessOutcome checkProcessOutcome(String name);

}
