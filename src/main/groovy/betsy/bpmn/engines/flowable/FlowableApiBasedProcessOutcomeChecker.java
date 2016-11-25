package betsy.bpmn.engines.flowable;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.engines.activiti.ActivitiEngine;
import org.json.JSONObject;

public class FlowableApiBasedProcessOutcomeChecker implements BPMNProcessInstanceOutcomeChecker {

    @Override
    public ProcessInstanceOutcome checkProcessOutcome(String name) {
        return isProcessDeployed(name) ? ProcessInstanceOutcome.OK : ProcessInstanceOutcome.UNDEPLOYED_PROCESS;
    }

    private boolean isProcessDeployed(String key) {
        String checkDeploymentUrl = Flowable5220Engine.URL + "/service/repository/deployments?name="+key+".bpmn";

        JSONObject result = JsonHelper.get(checkDeploymentUrl, 200);
        return result.getInt("size") == 1;
    }
}
