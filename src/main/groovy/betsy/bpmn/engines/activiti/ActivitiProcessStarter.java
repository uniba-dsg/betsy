package betsy.bpmn.engines.activiti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.Variables;
import pebl.test.steps.Variable;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class ActivitiProcessStarter implements BPMNProcessStarter {

    private static final Logger LOGGER = Logger.getLogger(ActivitiProcessStarter.class);

    @Override
    public void start(String processID, List<Variable> variables) throws RuntimeException {
        startProcess(processID, new Variables(variables).toArray());
    }

    public static void startProcess(String id, Object... variables) {
        LOGGER.info("Start process instance for " + id);
        String deploymentUrl = ActivitiEngine.URL + "/service/runtime/process-instances";

        Map<String, Object> request = new HashMap<>();
        request.put("processDefinitionKey", id);
        request.put("variables", variables);
        request.put("businessKey", "key-" + id);

        JSONObject jsonRequest = new JSONObject(request);
        LOGGER.info("With json message: " + jsonRequest.toString());

        JsonHelper.post(deploymentUrl, jsonRequest, 201);
    }
}
