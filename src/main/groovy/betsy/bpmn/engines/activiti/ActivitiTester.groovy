package betsy.bpmn.engines.activiti

import betsy.bpmn.engines.BPMNTester
import betsy.bpmn.engines.camunda.JsonHelper
import betsy.bpmn.model.BPMNTestCase
import betsy.bpmn.model.BPMNTestCaseVariable
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks
import org.apache.log4j.Logger
import org.json.JSONArray
import org.json.JSONObject

import java.nio.file.Path

class ActivitiTester {

    BPMNTestCase testCase
    String restURL
    Path reportPath
    Path testBin
    Path testSrc
    String key
    Path logDir

    /**
     * runs a single test
     */
    void runTest() {
        //make bin dir
        FileTasks.mkdirs(testBin)
        FileTasks.mkdirs(reportPath)

        if (!testCase.selfStarting) {
            startProcess(key, BPMNTestCaseVariable.mapToArrayWithMaps(testCase.variables));
        }

        WaitTasks.sleep(testCase.delay)

        BPMNTester.setupPathToToolsJarForJavacAntTask(this)
        BPMNTester.compileTest(testSrc, testBin)
        BPMNTester.executeTest(testSrc, testBin, reportPath)
    }

    private Path getFileName() {
        logDir.resolve("..").normalize().resolve("bin").resolve("log" + testCase.number + ".txt")
    }

    public static void startProcess(String id, Object[] variables) {
        log.info("Start process instance for " + id);
        String deploymentUrl = ActivitiEngine.URL + "/service/runtime/process-instances";

        Map<String, Object> request = new HashMap<>();
        request.put("processDefinitionKey", id);
        request.put("variables", variables)
        request.put("businessKey", "key-" + id)

        JSONObject jsonRequest = new JSONObject(request)
        log.info("With json message: " + jsonRequest.toString())

        JsonHelper.post(deploymentUrl, jsonRequest, 201)
    }

    public static String getIdByName(String key) {
        log.info("Get Process Definition By Key " + key);

        String deploymentUrl = ActivitiEngine.URL + "/service/repository/process-definitions?key=" + key;

        JSONObject jsonResponse = JsonHelper.get(deploymentUrl, 200);

        JSONObject object = jsonResponse;
        JSONArray dataObject = object.getJSONArray("data");
        JSONObject entry = dataObject.getJSONObject(0)
        return entry.get("id");
    }

    private static final Logger log = Logger.getLogger(ActivitiTester.class);

}
