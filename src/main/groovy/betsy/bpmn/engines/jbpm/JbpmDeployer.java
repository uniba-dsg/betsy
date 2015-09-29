package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.JsonHelper;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class JbpmDeployer {

    private static final Logger LOGGER = Logger.getLogger(JbpmDeployer.class);

    private final String deploymentId;
    private final String user = "admin";
    private final String password = "admin";
    private final String baseUrl;

    public JbpmDeployer(String baseUrl, String deploymentId) {
        this.deploymentId = deploymentId;
        this.baseUrl = baseUrl;
    }

    public void deploy() {
        LOGGER.info("Trying to deploy process \"" + deploymentId + "\".");
        JsonHelper.postWithAuthWithAcceptJson(baseUrl + "/rest/deployment/" + deploymentId + "/deploy", 202, user, password);
    }

    public void undeploy() {
        LOGGER.info("Trying to undeploy process \"" + deploymentId + "\".");
        JsonHelper.postWithAuthWithAcceptJson(baseUrl + "/rest/deployment/" + deploymentId + "/undeploy", 202, user, password);
    }

    public boolean isDeployed() {
        LOGGER.info("Trying to check the deployment status of process \"" + deploymentId + "\".");

        try {

            JSONObject object = JsonHelper.getJSONWithAuth(baseUrl + "/rest/deployment/" + deploymentId, 200, user, password);
            String status = object.getString("status");
            return status != null && "DEPLOYED".equals(status);

        } catch (Exception e) {
            LOGGER.error("error: " + e.getMessage(), e);
            return false;
        }
    }

}
