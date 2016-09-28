package betsy.bpmn.engines.camunda;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;
import org.json.JSONArray;

public class CamundaApiBasedProcessInstanceOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    private final String restURL;

    public CamundaApiBasedProcessInstanceOutcomeChecker() {
        this.restURL = "http://localhost:8080/engine-rest/engine/default";
    }

    @Override
    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        return isProcessDeployed(name) ? ProcessInstanceOutcome.OK : ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
    }

    private boolean isProcessDeployed(String key) {
        JSONArray result = JsonHelper.getJsonArray(restURL+"/process-definition", 200);

        for(int i=0; i<result.length(); i++) {
            if((key+".bpmn").equals(result.getJSONObject(i).get("resource"))) {
                return true;
            }
        }

        return false;
    }
}
