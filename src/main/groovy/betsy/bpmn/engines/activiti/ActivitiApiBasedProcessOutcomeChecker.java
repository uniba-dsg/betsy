package betsy.bpmn.engines.activiti;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;
import org.json.JSONException;
import org.json.JSONObject;


public class ActivitiApiBasedProcessOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    @Override
    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        return isProcessDeployed(name) ? ProcessInstanceOutcome.OK : ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
    }

    private boolean isProcessDeployed(String key) {
        String checkDeploymentUrl = ActivitiEngine.URL + "/service/repository/deployments?name="+key+".bpmn";

        JSONObject result = JsonHelper.get(checkDeploymentUrl, 200);
        try {
            return result.getInt("size") == 1;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
