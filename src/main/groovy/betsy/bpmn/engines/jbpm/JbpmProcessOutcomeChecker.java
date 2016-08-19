package betsy.bpmn.engines.jbpm;

import java.util.Objects;
import java.util.Optional;

import betsy.bpmn.engines.BPMNProcessOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.BPMNAssertions;
import org.apache.log4j.Logger;

public class JbpmProcessOutcomeChecker implements BPMNProcessOutcomeChecker {

    private static final Logger LOGGER = Logger.getLogger(JbpmProcessOutcomeChecker.class);

    private final String user;
    private final String password;
    private final String requestUrl;


    public static JbpmProcessOutcomeChecker build(String deploymentID) {
        return new JbpmProcessOutcomeChecker("http://localhost:8080/jbpm-console" + "/rest/runtime/" + deploymentID + "/history/instance/1");
    }

    public static JbpmProcessOutcomeChecker build() {
        return new JbpmProcessOutcomeChecker("http://localhost:8080/jbpm-console" + "/rest/history/instance/1");
    }

    public JbpmProcessOutcomeChecker(String requestUrl) {
        this.user = "admin";
        this.password = "admin";
        this.requestUrl = Objects.requireNonNull(requestUrl);
    }

    public ProcessOutcome checkProcessOutcome(String name) {
        try {
            LOGGER.info("Trying to check process result status for " + name);
            String result = JsonHelper.getStringWithAuth(requestUrl, 200, user, password);
            if (result.contains("ERR-1")) {
                LOGGER.info("Process has been aborted. Error with id ERR-1 detected.");
                return ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            } else if (result.contains("ESC_1")) {
                LOGGER.info("Process has been aborted. Escalation with id ESC_1 detected.");
                return ProcessOutcome.PROCESS_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN;
            } else if (result.contains("<status>3</status>")) {
                LOGGER.info("Process has been aborted with unknown error.");
                return ProcessOutcome.PROCESS_ABORTED;
            } else if (result.contains("<status>1</status>")) {
                LOGGER.info("Process completed normally.");
                return ProcessOutcome.OK;
            }

        } catch (RuntimeException innerEx) {
            LOGGER.info("Checking process result status failed.", innerEx);
            return ProcessOutcome.COULD_NOT_CHECK_PROCESS_STATUS;
        }

        return ProcessOutcome.UNKNOWN;
    }

}
