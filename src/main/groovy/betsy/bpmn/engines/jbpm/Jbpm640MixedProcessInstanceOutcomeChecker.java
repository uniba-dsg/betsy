package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;
import java.util.List;

import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.JsonHelper;

public class Jbpm640MixedProcessInstanceOutcomeChecker extends JbpmApiBasedProcessInstanceOutcomeChecker {

    private final Path logFile;

    public Jbpm640MixedProcessInstanceOutcomeChecker(String requestUrl, String deploymentUrl, Path logFile) {
        super(requestUrl, deploymentUrl);
        this.logFile = logFile;
    }

    @Override
    public boolean isProcessDeployed() {
        // check using .../processes
        String result = JsonHelper.getStringWithAuth(processDeploymentUrl, 200, user, password);
        if(result.contains("<process-definition>")) {
            return true;
        }

        // otherwise
        result = JsonHelper.getStringWithAuth(processDeploymentUrl.replace("/processes", ""), 200, user, password);
        if(!(result.contains("<deployment-status>DEPLOYED</deployment-status>") ||
                result.contains("<status>DEPLOYED</status>"))) {
            return false;
        }

        // check using log files
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
