package betsy.bpmn.engines.jbpm;

import java.util.List;

import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.Variable;
import betsy.common.tasks.WaitTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class JbpmProcessStarter implements BPMNProcessStarter {

    private static final Logger LOGGER = Logger.getLogger(JbpmProcessStarter.class);

    private final String user;
    private final String password;
    private final String requestUrl;

    public JbpmProcessStarter(String deploymentID) {
        user = "admin";
        password = "admin";
        requestUrl = "http://localhost:8080/jbpm-console" + "/rest/runtime/" + deploymentID + "/process/";
    }

    @Override
    public void start(String processName, List<Variable> variables) throws RuntimeException {
        String queryParameter = Variable.toQueryParameter(variables);

        String processStartEquestURL = requestUrl + processName + "/start" + queryParameter;
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

}
