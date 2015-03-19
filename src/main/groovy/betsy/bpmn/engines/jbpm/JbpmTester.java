package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.LogFileAnalyzer;
import betsy.bpmn.engines.OverlappingTimestampChecker;
import betsy.bpmn.engines.camunda.JsonHelper;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestVariable;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class JbpmTester {
    /**
     * Runs a single test
     */
    public void runTest() {
        addDeploymentErrorsToLogFile(serverLogFile);

        //setup variables and start process
        Map<String, Object> variables = new HashMap<>();
        for (BPMNTestVariable variable : testCase.getVariables()) {
            variables.put(variable.getName(), variable.getValue());
        }

        StringJoiner joiner = new StringJoiner("&", "?", "");
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            joiner.add("map_" + entry.getKey() + "=" + entry.getValue());
        }

        String requestUrl = processStartUrl + joiner.toString();
        try {
            LOGGER.info("Trying to start process \"" + name + "\".");
            JsonHelper.postStringWithAuth(requestUrl, new JSONObject(), 200, user, password);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("No runtime manager could be found")) {
                LOGGER.info("Instantiation failed as no runtime manager could be found. Retrying in 10000ms.");
                //retry after delay
                WaitTasks.sleep(10000);
                try {
                    JsonHelper.postStringWithAuth(requestUrl, new JSONObject(), 200, user, password);
                } catch (RuntimeException innerEx) {
                    LOGGER.info(BPMNAssertions.ERROR_RUNTIME + ": Instantiation still not possible. Aborting test.", innerEx);
                    BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
                }
            } else {
                LOGGER.info(BPMNAssertions.ERROR_RUNTIME + ": Instantiation of process failed. Reason:", ex);
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            }
        }

        //delay for timer intermediate event
        WaitTasks.sleep(testCase.getDelay().orElse(0));

        // Check on parallel execution
        checkParallelExecution();
        checkProcessOutcome();

        bpmnTester.test();
    }

    private void checkProcessOutcome() {
        try {
            LOGGER.info("Trying to check process result status for " + name);
            String result = JsonHelper.getStringWithAuth(processHistoryUrl, 200, user, password);
            if (result.contains("ERR-1")) {
                LOGGER.info("Process has been aborted. Error with id ERR-1 detected.");
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
            } else if (result.contains("ESC_1")) {
                LOGGER.info("Process has been aborted. Escalation with id ESC_1 detected.");
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
            } else if (result.contains("<status>3</status>")) {
                LOGGER.info("Process has been aborted with unknown error.");
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_PROCESS_ABORTED);
            } else if (result.contains("<status>1</status>")) {
                LOGGER.info("Process completed normally.");
            }

        } catch (RuntimeException innerEx) {
            LOGGER.info("Checking process result status failed.", innerEx);
        }
    }

    private void addDeploymentErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("failed to deploy", BPMNAssertions.ERROR_DEPLOYMENT);
        for (BPMNAssertions deploymentError : analyzer.getErrors()) {
            BPMNAssertions.appendToFile(getFileName(), deploymentError);
            LOGGER.info(BPMNAssertions.ERROR_DEPLOYMENT + ": " + deploymentId + ", " + name + ": Deployment error detected.");
        }
    }

    private void checkParallelExecution() {
        // Only check on parallelism when asked for a parallel assertion
        boolean isCheckRequired = testCase.getAssertions().contains(BPMNAssertions.EXECUTION_PARALLEL.toString());
        if (!isCheckRequired) {
            return;
        }

        Integer testCaseNum = testCase.getNumber();
        String testCaseNumber = testCaseNum.toString();

        Path logParallelOne = getFileName().getParent().resolve("log" + testCaseNumber + "_parallelOne.txt");
        Path logParallelTwo = getFileName().getParent().resolve("log" + testCaseNumber + "_parallelTwo.txt");

        try {
            OverlappingTimestampChecker otc = new OverlappingTimestampChecker(getFileName(), logParallelOne, logParallelTwo);
            otc.checkParallelism();
        } catch (IllegalArgumentException e) {
            LOGGER.info("Could not validate parallel execution", e);
        }
    }

    private Path getFileName() {
        return logDir.resolve("log" + testCase.getNumber() + ".txt");
    }

    public BPMNTestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(BPMNTestCase testCase) {
        this.testCase = testCase;
    }

    public String getProcessStartUrl() {
        return processStartUrl;
    }

    public void setProcessStartUrl(String processStartUrl) {
        this.processStartUrl = processStartUrl;
    }

    public String getProcessHistoryUrl() {
        return processHistoryUrl;
    }

    public void setProcessHistoryUrl(String processHistoryUrl) {
        this.processHistoryUrl = processHistoryUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public Path getLogDir() {
        return logDir;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public Path getServerLogFile() {
        return serverLogFile;
    }

    public void setServerLogFile(Path serverLogFile) {
        this.serverLogFile = serverLogFile;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private BPMNTestCase testCase;
    private String processStartUrl;
    private String processHistoryUrl;
    private String name;
    private String deploymentId;

    public void setBpmnTester(BPMNTester bpmnTester) {
        this.bpmnTester = bpmnTester;
    }

    private BPMNTester bpmnTester;
    private Path logDir;
    private Path serverLogFile;
    private String user = "admin";
    private String password = "admin";

    private static final Logger LOGGER = Logger.getLogger(JbpmTester.class);

}
