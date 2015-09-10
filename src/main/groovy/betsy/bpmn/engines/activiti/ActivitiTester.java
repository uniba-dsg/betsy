package betsy.bpmn.engines.activiti;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.LogFileAnalyzer;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestVariable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ActivitiTester {
    private static final Logger LOGGER = Logger.getLogger(ActivitiTester.class);
    private BPMNTestCase testCase;
    private String key;
    private Path logDir;
    private BPMNTester bpmnTester;

    public static void startProcess(String id, Object[] variables) {
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

    /**
     * runs a single test
     */
    public void runTest() {
        Path logFile = logDir.resolve("activiti.log");

        addDeploymentErrorsToLogFile(logFile);

        try {
            startProcess(key, BPMNTestVariable.mapToArrayWithMaps(testCase.getVariables()));

            // Wait and check for errors only if process instantiation was successful
            WaitTasks.sleep(testCase.getDelay().orElse(0));
            addRuntimeErrorsToLogFile(logFile);

            // Check on parallel execution
            BPMNEnginesUtil.checkParallelExecution(testCase, getFileName());

            // Check data type
            BPMNEnginesUtil.checkDataLog(testCase, getFileName());
        } catch (Exception e) {
            LOGGER.info("Could not start process", e);
            addRuntimeErrorsToLogFile(logFile);
        }

        BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, getFileName());

        LOGGER.info("contents of log file " + getFileName() + ": " + FileTasks.readAllLines(getFileName()));

        bpmnTester.test();
    }

    private void addDeploymentErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("Ignoring unsupported activity type", BPMNAssertions.ERROR_DEPLOYMENT);
        analyzer.addSubstring("org.activiti.engine.ActivitiException: Errors while parsing:", BPMNAssertions.ERROR_DEPLOYMENT);
        for (BPMNAssertions deploymentError : analyzer.getErrors()) {
            BPMNAssertions.appendToFile(getFileName(), deploymentError);
        }
    }

    private void addRuntimeErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("org.activiti.engine.ActivitiException", BPMNAssertions.ERROR_RUNTIME);
        analyzer.addSubstring("EndEvent_2 throws error event with errorCode 'ERR-1'", BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
        analyzer.addSubstring("No catching boundary event found for error with errorCode 'ERR-1'", BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
        for (BPMNAssertions runtimeError : analyzer.getErrors()) {
            BPMNAssertions.appendToFile(getFileName(), runtimeError);
        }
    }

    private Path getFileName() {
        return logDir.resolve("..").normalize().resolve("bin").resolve("log" + testCase.getNumber() + ".txt");
    }

    public BPMNTestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(BPMNTestCase testCase) {
        this.testCase = testCase;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public void setBpmnTester(BPMNTester bpmnTester) {
        this.bpmnTester = bpmnTester;
    }
}
