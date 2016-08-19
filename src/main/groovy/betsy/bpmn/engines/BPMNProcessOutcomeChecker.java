package betsy.bpmn.engines;

import java.util.Optional;

import betsy.bpmn.model.BPMNAssertions;

public interface BPMNProcessOutcomeChecker {

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
