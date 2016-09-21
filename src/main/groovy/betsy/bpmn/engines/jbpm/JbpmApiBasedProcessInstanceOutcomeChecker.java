package betsy.bpmn.engines.jbpm;

import java.util.Objects;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class JbpmApiBasedProcessInstanceOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    private static final Logger LOGGER = Logger.getLogger(JbpmApiBasedProcessInstanceOutcomeChecker.class);

    private final String user;
    private final String password;
    private final String requestUrl;


    public static JbpmApiBasedProcessInstanceOutcomeChecker buildWithDeploymentId() {
        return new JbpmApiBasedProcessInstanceOutcomeChecker("http://localhost:8080/jbpm-console" + "/rest/runtime/" + getDeploymentID() + "/history/instance/1");
    }

    public static String getDeploymentID() {
        JSONArray json = JsonHelper.getJSONWithAuthAsArray("http://localhost:8080/jbpm-console" + "/rest/deployment/", 200, "admin", "admin");
        if(json.length() == 0) {
            return "";
        }
        JSONObject jsonObject = json.optJSONObject(0);
        if(jsonObject.has("deploymentUnitList")) {
            JSONObject firstElement = jsonObject.optJSONArray("deploymentUnitList").getJSONObject(0);
            if(firstElement.has("deployment-unit")) {
                JSONObject deploymentUnit = firstElement.optJSONObject("deployment-unit");
                return getDeploymentID(deploymentUnit);
            } else {
                return getDeploymentID(firstElement);
            }
        } else {
            return getDeploymentID(jsonObject);
        }
    }

    private static String getDeploymentID(JSONObject deploymentUnit) {
        return deploymentUnit.optString("groupId") + ":" + deploymentUnit.optString("artifactId") + ":" + deploymentUnit.optString("version");
    }

    public static JbpmApiBasedProcessInstanceOutcomeChecker build() {
        return new JbpmApiBasedProcessInstanceOutcomeChecker("http://localhost:8080/jbpm-console" + "/rest/history/instance/1");
    }

    public JbpmApiBasedProcessInstanceOutcomeChecker(String requestUrl) {
        this.user = "admin";
        this.password = "admin";
        this.requestUrl = Objects.requireNonNull(requestUrl);
    }

    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        try {
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

}
