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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ActivitiTester {
    private static final Logger LOGGER = Logger.getLogger(ActivitiTester.class);
    private BPMNTestCase testCase;
    private String key;
    private Path logDir;
    private BPMNTester bpmnTester;

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

    /**
     * runs a single test
     */
    public void runTest() {
        Path logFile = logDir.resolve("activiti.log");

        addDeploymentErrorsToLogFile(logFile);

        // skip execution if deployment is expected to fail
        if(testCase.getAssertions().contains(BPMNAssertions.ERROR_DEPLOYMENT.toString())) {
            LOGGER.info("Skipping execution of process as deployment is expected to have failed.");
            // if deployment has not failed the logX.txt file has to be generated for further test processing
            if(!Files.exists(getFileName())) {
                try {
                    Files.createFile(getFileName());
                } catch (IOException e) {
                    LOGGER.warn("Creation of file "+getFileName()+" failed.", e);
                }
            }
        } else {
            // try execution of deployed process
            try {
                if (testCase.hasParallelProcess()) {
                    startProcess(BPMNTestCase.PARALLEL_PROCESS_KEY);
                }

                startProcess(key, BPMNTestVariable.mapToArrayWithMaps(testCase.getVariables()));

                // Wait and check for errors only if process instantiation was successful
                WaitTasks.sleep(testCase.getDelay().orElse(0));
                addRuntimeErrorsToLogFile(logFile);

                // Check on parallel execution
                BPMNEnginesUtil.checkParallelExecution(testCase, getFileName());

                // Check whether MARKER file exists
                BPMNEnginesUtil.checkMarkerFileExists(testCase, getFileName());

                // Check data type
                BPMNEnginesUtil.checkDataLog(testCase, getFileName());
            } catch (Exception e) {
                LOGGER.info("Could not start process", e);
                addRuntimeErrorsToLogFile(logFile);
            }

        }
        BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, getFileName());

        LOGGER.info("contents of log file " + getFileName() + ": " + FileTasks.readAllLines(getFileName()));

        bpmnTester.test();
    }

    private void addDeploymentErrorsToLogFile(Path logFile) {

        LOGGER.info("Checking whether deployment was successful.");
        String checkDeploymentUrl = ActivitiEngine.URL + "/service/repository/deployments?name="+key+".bpmn";

        JSONObject result = JsonHelper.get(checkDeploymentUrl, 200);
        if(result.getInt("size")!=1) {
            LOGGER.info("Deployment of process'"+key+"' failed.");
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_DEPLOYMENT);
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
