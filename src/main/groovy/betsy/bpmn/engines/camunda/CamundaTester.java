package betsy.bpmn.engines.camunda;

import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.LogFileAnalyzer;
import betsy.bpmn.engines.OverlappingTimestampChecker;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestVariable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CamundaTester {

    private static final Logger LOGGER = Logger.getLogger(CamundaTester.class);

    private BPMNTestCase testCase;
    private String restURL;
    private String key;
    private Path logDir;
    private BPMNTester bpmnTester;

    /**
     * runs a single test
     */
    public void runTest() {

        Path logFile = FileTasks.findFirstMatchInFolder(logDir, "catalina*");

        addDeploymentErrorsToLogFile(logFile);

        try {

            //first request to get id
            JSONObject response = JsonHelper.get(restURL + "/process-definition?key=" + key, 200);
            final String id = String.valueOf(response.get("id"));

            //assembling JSONObject for second request
            JSONObject requestBody = new JSONObject();
            requestBody.put("variables", mapToArrayWithMaps(testCase.getVariables()));
            requestBody.put("businessKey", "key-" + key);

            //second request to start process using id and Json to get the process instance id
            JsonHelper.post(restURL + "/process-definition/" + id + "/start?key=" + key, requestBody, 200);

            // Wait and check for Errors only if instantiation was successful
            WaitTasks.sleep(testCase.getDelay().orElse(0));
            addRuntimeErrorsToLogFile(logFile);
            checkParallelExecution();
        } catch (Exception e) {
            LOGGER.info("Could not start process", e);
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
        }


        bpmnTester.test();
    }

    public void setBpmnTester(BPMNTester bpmnTester) {
        this.bpmnTester = bpmnTester;
    }

    private void addDeploymentErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("Ignoring unsupported activity type", BPMNAssertions.ERROR_DEPLOYMENT);
        analyzer.addSubstring("org.camunda.bpm.engine.ProcessEngineException", BPMNAssertions.ERROR_DEPLOYMENT);
        for (BPMNAssertions deploymentError : analyzer.getErrors()) {
            BPMNAssertions.appendToFile(getFileName(), deploymentError);
        }
    }

    private void addRuntimeErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("org.camunda.bpm.engine.ProcessEngineException", BPMNAssertions.ERROR_RUNTIME);
        analyzer.addSubstring("EndEvent_2 throws error event with errorCode 'ERR-1'", BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
        for (BPMNAssertions runtimeError : analyzer.getErrors()) {
            BPMNAssertions.appendToFile(getFileName(), runtimeError);
        }
    }

    private void checkParallelExecution() {
        Integer testCaseNum = new Integer(testCase.getNumber());
        String testCaseNumber = testCaseNum.toString();

        Path logParallelOne = FileTasks.findFirstMatchInFolder(getFileName().getParent(), "log" + testCaseNumber + "_parallelOne.txt");
        Path logParallelTwo = FileTasks.findFirstMatchInFolder(getFileName().getParent(), "log" + testCaseNumber + "_parallelTwo.txt");

        try {
            OverlappingTimestampChecker otc = new OverlappingTimestampChecker(getFileName(), logParallelOne, logParallelTwo);
            otc.checkParallelism();
        } catch (IllegalArgumentException e) {
            LOGGER.info("Could not validate parallel execution", e);
        }
    }

    public static Map<String, Object> mapToArrayWithMaps(List<BPMNTestVariable> variables) {
        Map<String, Object> map = new HashMap<>();

        for (BPMNTestVariable entry : variables) {
            Map<String, Object> submap = new HashMap<>();
            submap.put("value", entry.getValue());
            submap.put("type", entry.getType());
            map.put(entry.getName(), submap);
        }

        return map;
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

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Path getLogDir() {
        return logDir;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

}
