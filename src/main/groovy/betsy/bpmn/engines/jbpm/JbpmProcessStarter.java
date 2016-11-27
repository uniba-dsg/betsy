package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.Variables;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pebl.benchmark.test.steps.vars.Variable;

public class JbpmProcessStarter implements BPMNProcessStarter {

    private static final Logger LOGGER = Logger.getLogger(JbpmProcessStarter.class);

    private static final Pattern LOG_FILE_EXTRACT_DEPLOYMENT_ID = Pattern.compile("DEPLOY job for \\[([^\\]]*)\\]");

    private final String user;
    private final String password;
    private final String requestUrl;
    private final Path serverLog;

    public JbpmProcessStarter(Path serverLog) {
        this.serverLog = Objects.requireNonNull(serverLog);
        user = "admin";
        password = "admin";
        requestUrl = "http://localhost:8080/jbpm-console";
    }

    @Override
    public void start(String processName, List<Variable> variables) throws RuntimeException {
        // determine deployment
        String deploymentID = getDeploymentID(requestUrl, user, password);

        if (Objects.equals(deploymentID, "")) {
            deploymentID = FileTasks.readAllLines(serverLog)
                    .stream()
                    .map(line -> {
                        Matcher matcher = LOG_FILE_EXTRACT_DEPLOYMENT_ID.matcher(line);
                        if (matcher.find()) {
                            return matcher.group(1);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .findFirst().orElse("");
        }

        if (Objects.equals(deploymentID, "")) {
            LOGGER.error("Cannot start as the deployment ID cannot be determined for " + processName + " with " + variables);
            return;
        }

        String queryParameter = new Variables(variables).toQueryParameter();
        String processStartRequestURL = requestUrl + "/rest/runtime/" + deploymentID + "/process/" + processName + "/start" + queryParameter;
        try {
            LOGGER.info("Trying to start process \"" + processName + "\".");
            JsonHelper.postStringWithAuth(processStartRequestURL, new JSONObject(), 200, user, password);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("No runtime manager could be found")) {
                LOGGER.info("Instantiation failed as no runtime manager could be found. Retrying in " + TimeoutRepository.getTimeout("JbpmTester.runTest").getTimeoutInMs() + "ms.");
                //retry after delay
                WaitTasks.sleep(TimeoutRepository.getTimeout("JbpmTester.runTest").getTimeoutInMs());
                try {
                    JsonHelper.postStringWithAuth(processStartRequestURL, new JSONObject(), 200, user, password);
                } catch (RuntimeException innerEx) {
                    LOGGER.info("Runtime error: Instantiation still not possible. Aborting test.", innerEx);
                    throw new RuntimeException(innerEx);
                }
            } else {
                LOGGER.info("Runtime error: Instantiation of process failed. Reason:", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    public static String getDeploymentID(String requestUrl, String user, String password) {
        JSONArray json = JsonHelper.getJSONWithAuthAsArray(requestUrl + "/rest/deployment/", 200, user, password);
        if (json.length() == 0) {
            return "";
        }
        JSONObject jsonObject = json.optJSONObject(0);
        if (jsonObject.has("deploymentUnitList")) {
            JSONArray deploymentUnitList = jsonObject.optJSONArray("deploymentUnitList");
            if (deploymentUnitList.length() == 0) {
                LOGGER.error("Could not retrieve deployment ID");
                return "";
            }

            JSONObject firstElement = null;
            try {
                firstElement = deploymentUnitList.getJSONObject(0);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (firstElement.has("deployment-unit")) {
                JSONObject deploymentUnit = firstElement.optJSONObject("deployment-unit");
                return getDeploymentID(deploymentUnit);
            } else {
                return getDeploymentID(firstElement);
            }
        } else {
            return getDeploymentID(jsonObject);
        }
    }

    public static String getDeploymentID(JSONObject deploymentUnit) {
        return deploymentUnit.optString("groupId") + ":" + deploymentUnit.optString("artifactId") + ":" + deploymentUnit.optString("version");
    }

}
