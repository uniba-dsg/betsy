package betsy.bpmn.engines.jbpm;

import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;
import org.apache.log4j.Logger;

public class JbpmApiBasedProcessInstanceOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    private static final Logger LOGGER = Logger.getLogger(JbpmApiBasedProcessInstanceOutcomeChecker.class);

    protected final String user;
    protected final String password;
    private final String requestUrl;

    protected final String processDeploymentUrl;


    public JbpmApiBasedProcessInstanceOutcomeChecker(String requestUrl, String deploymentUrl) {
        this.user = "admin";
        this.password = "admin";
        this.requestUrl = Objects.requireNonNull(requestUrl);
        this.processDeploymentUrl = deploymentUrl;
    }

    @Override
    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        try {
            // Check deployment status first
            if (!isProcessDeployed()) {
                return ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
            }

            LOGGER.info("Trying to check process result status for " + name);
            String result = JsonHelper.getStringWithAuth(requestUrl, 200, user, password);
            if (result.contains("ERR-1")) {
                LOGGER.info("Process has been aborted. Error with id ERR-1 detected.");
                return ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN;
            } else if (result.contains("ESC_1")) {
                LOGGER.info("Process has been aborted. Escalation with id ESC_1 detected.");
                return ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN;
            } else if (result.contains("<status>3</status>")) {
                LOGGER.info("Process has been aborted with unknown error.");
                return ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED;
            } else if (result.contains("<status>1</status>")) {
                LOGGER.info("Process completed normally.");
                return ProcessInstanceOutcome.OK;
            }
        } catch (RuntimeException innerEx) {
            LOGGER.info("Checking process result status failed.", innerEx);
            return ProcessInstanceOutcome.COULD_NOT_CHECK_PROCESS_INSTANCE_STATUS;
        }

        return ProcessInstanceOutcome.UNKNOWN;
    }

    public boolean isProcessDeployed() {
        String result = JsonHelper.getStringWithAuth(processDeploymentUrl, 200, user, password);
        return result.contains("<deployment-status>DEPLOYED</deployment-status>") ||
                result.contains("<status>DEPLOYED</status>");
    }
}
