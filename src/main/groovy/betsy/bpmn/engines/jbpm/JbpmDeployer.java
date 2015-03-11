package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.camunda.JsonHelper;
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

        // issue: https://developer.jboss.org/thread/236369?start=0&tstart=0
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

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080/jbpm-console";
        String deploymentId = "de.uniba.dsg:SequenceFlow:1.0";
        System.out.println(new JbpmDeployer(deploymentId, baseUrl).isDeployed());
        new JbpmDeployer(deploymentId, baseUrl).deploy();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new JbpmDeployer(deploymentId, baseUrl).isDeployed());
        new JbpmDeployer(deploymentId, baseUrl).undeploy();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new JbpmDeployer(deploymentId, baseUrl).isDeployed());
    }

}
