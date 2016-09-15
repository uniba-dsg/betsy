package betsy.bpmn.engines.jbpm;

import java.util.List;

import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.Variables;
import betsy.bpmn.model.BPMNAssertions;
import pebl.test.steps.vars.Variable;
import betsy.common.tasks.WaitTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class JbpmProcessStarter implements BPMNProcessStarter {

    private static final Logger LOGGER = Logger.getLogger(JbpmProcessStarter.class);

    private final String user;
    private final String password;
    private final String requestUrl;

    public JbpmProcessStarter() {
        user = "admin";
        password = "admin";
        requestUrl = "http://localhost:8080/jbpm-console";
    }

    @Override
    public void start(String processName, List<Variable> variables) throws RuntimeException {
        // determine deployment
        String deploymentID = getDeploymentID();

        String queryParameter = new Variables(variables).toQueryParameter();
        String processStartEquestURL = requestUrl + "/rest/runtime/" + deploymentID + "/process/" + processName + "/start" + queryParameter;
        try {
            LOGGER.info("Trying to start process \"" + processName + "\".");
            JsonHelper.postStringWithAuth(processStartEquestURL, new JSONObject(), 200, user, password);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("No runtime manager could be found")) {
                LOGGER.info("Instantiation failed as no runtime manager could be found. Retrying in "+ TimeoutRepository.getTimeout("JbpmTester.runTest").getTimeoutInMs() +"ms.");
                //retry after delay
                WaitTasks.sleep(TimeoutRepository.getTimeout("JbpmTester.runTest").getTimeoutInMs());
                try {
                    JsonHelper.postStringWithAuth(processStartEquestURL, new JSONObject(), 200, user, password);
                } catch (RuntimeException innerEx) {
                    LOGGER.info(BPMNAssertions.ERROR_RUNTIME + ": Instantiation still not possible. Aborting test.", innerEx);
                    throw new RuntimeException(innerEx);
                }
            } else {
                LOGGER.info(BPMNAssertions.ERROR_RUNTIME + ": Instantiation of process failed. Reason:", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    private String getDeploymentID() {
        JSONArray json = JsonHelper.getJSONWithAuthAsArray(requestUrl + "/rest/deployment/", 200, user, password);
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

    private String getDeploymentID(JSONObject deploymentUnit) {
        return deploymentUnit.optString("groupId") + ":" + deploymentUnit.optString("artifactId") + ":" + deploymentUnit.optString("version");
    }

}
