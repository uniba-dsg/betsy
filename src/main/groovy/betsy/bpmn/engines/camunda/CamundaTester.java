package betsy.bpmn.engines.camunda;

import betsy.bpmn.engines.*;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestVariable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

                startProcess(key);

                // Wait and check for Errors only if instantiation was successful
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
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            }
        }

        BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, getFileName());

        LOGGER.info("contents of log file " + getFileName() + ": " + FileTasks.readAllLines(getFileName()));

        bpmnTester.test();
    }

    private void startProcess(String key) {
        //first request to get id
        JSONObject response = JsonHelper.get(restURL + "/process-definition?key=" + key, 200);
        final String id = String.valueOf(response.get("id"));

        //assembling JSONObject for second request
        JSONObject requestBody = new JSONObject();
        requestBody.put("variables", mapToArrayWithMaps(testCase.getVariables()));
        requestBody.put("businessKey", "key-" + key);

        //second request to start process using id and Json to get the process instance id
        JsonHelper.post(restURL + "/process-definition/" + id + "/start?key=" + key, requestBody, 200);
    }

    public void setBpmnTester(BPMNTester bpmnTester) {
        this.bpmnTester = bpmnTester;
    }

    private void addDeploymentErrorsToLogFile(Path logFile) {

        if(!isProcessDeployed()) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_DEPLOYMENT);
        }
    }

    private boolean isProcessDeployed() {
        JSONArray result = JsonHelper.getJsonArray(restURL+"/process-definition", 200);

        for(int i=0; i<result.length(); i++) {
            if((key+".bpmn").equals(result.getJSONObject(i).get("resource"))) {
                return true;
            }
        }

        return false;
    }

    private void addRuntimeErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("org.camunda.bpm.engine.ProcessEngineException", BPMNAssertions.ERROR_RUNTIME);
        analyzer.addSubstring("EndEvent_2 throws error event with errorCode 'ERR-1'", BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
        analyzer.addSubstring("'EndEvent_2' throws an error event with errorCode 'ERR-1'", BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
        for (BPMNAssertions runtimeError : analyzer.getErrors()) {
            BPMNAssertions.appendToFile(getFileName(), runtimeError);
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

    public void setRestURL(String restURL) {
        this.restURL = restURL;
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

}
