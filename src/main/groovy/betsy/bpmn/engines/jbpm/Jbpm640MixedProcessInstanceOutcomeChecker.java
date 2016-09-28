package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;
import java.util.List;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;

public class Jbpm640MixedProcessInstanceOutcomeChecker extends JbpmApiBasedProcessInstanceOutcomeChecker {

    private final Path logFile;

    public Jbpm640MixedProcessInstanceOutcomeChecker(String requestUrl, String deploymentUrl, String deploymentId, Path logFile) {
        super(requestUrl, deploymentUrl, deploymentId);
        this.logFile = logFile;
    }

    @Override
    public boolean isProcessDeployed() {
        String result = JsonHelper.getStringWithAuth(processDeploymentUrl, 200, user, password);
        if(result.contains("<process-definition>")) {
            return true;
        } else {
            List<String> lines = BPMNProcessInstanceOutcomeChecker.getLines(logFile);
            for (String line : lines) {
                if (line.contains("failed to deploy")) {
                    return false;
                } else if (line.contains("Unable to deploy")) {
                    return false;
                } else if (line.contains("ProcessLoadError")) {
                    return false;
                } else if (line.contains("Unable to build KieBaseModel:defaultKieBase")) {
                    return false;
                }
            }
            return true;
        }
    }
}
