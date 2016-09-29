package betsy.bpmn.engines.camunda;

import java.util.List;

import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.Variables;
import org.apache.log4j.Logger;
import pebl.benchmark.test.steps.vars.Variable;
import org.json.JSONObject;

public class CamundaProcessStarter implements BPMNProcessStarter {

    private static final Logger LOGGER = Logger.getLogger(CamundaProcessStarter.class);

    private final String restURL;

    public CamundaProcessStarter() {
        restURL = "http://localhost:8080/engine-rest/engine/default";
    }

    @Override
    public void start(String processID, List<Variable> variables) throws RuntimeException {
        LOGGER.info("Start process with processID=" + processID + " and the variables: " + variables);

        //first request to get id
        JSONObject response = JsonHelper.get(restURL + "/process-definition?key=" + processID, 200);
        final String id = String.valueOf(response.get("id"));

        //assembling JSONObject for second request
        JSONObject requestBody = new JSONObject();
        requestBody.put("variables", new Variables(variables).toMap());
        requestBody.put("businessKey", "key-" + processID);

        //second request to start process using id and Json to get the process instance id
        JsonHelper.post(restURL + "/process-definition/" + id + "/start?key=" + processID, requestBody, 200);
    }

}
