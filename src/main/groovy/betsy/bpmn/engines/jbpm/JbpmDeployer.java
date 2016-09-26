package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.JsonHelper;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class JbpmDeployer {

    private static final Logger LOGGER = Logger.getLogger(JbpmDeployer.class);

    private final String deploymentId;
    private static final String USER = "admin";
    private static final String PASSWORD = "admin";
    private final String baseUrl;

    public JbpmDeployer(String baseUrl, String deploymentId) {
        this.deploymentId = deploymentId;
        this.baseUrl = baseUrl;
    }

    public void deploy() {
        LOGGER.info("Trying to deploy process \"" + deploymentId + "\".");
        try {
            JsonHelper.postWithAuthWithAcceptJson(baseUrl + "/rest/deployment/" + deploymentId + "/deploy", 202, USER, PASSWORD);
        } catch (RuntimeException e) {
            LOGGER.error("Deployment failure", e);
        }
    }

    public void undeploy() {
        LOGGER.info("Trying to undeploy process \"" + deploymentId + "\".");
        try {
            JsonHelper.postWithAuthWithAcceptJson(baseUrl + "/rest/deployment/" + deploymentId + "/undeploy", 202, USER, PASSWORD);
        } catch (RuntimeException e) {
            LOGGER.error("Undeployment failure", e);
        }
    }

    public boolean isDeployed() {
        LOGGER.info("Trying to check the deployment status of process \"" + deploymentId + "\".");

        try {
            JSONObject object = JsonHelper.getJSONWithAuth(baseUrl + "/rest/deployment/" + deploymentId, 200, USER, PASSWORD);
            String status = object.optString("status");
            return "DEPLOYED".equalsIgnoreCase(status) || "DEPLOY_FAILED".equalsIgnoreCase(status);
        } catch (Exception e) {
            LOGGER.error("error: " + e.getMessage());
            return false;
        }
    }

}
