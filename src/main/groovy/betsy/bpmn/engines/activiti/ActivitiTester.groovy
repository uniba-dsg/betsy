package betsy.bpmn.engines.activiti

import betsy.bpmn.engines.BPMNTester
import betsy.bpmn.engines.camunda.JsonHelper
import betsy.bpmn.model.BPMNTestCase
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import org.apache.log4j.Logger
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

        //first request to get id

        JSONObject response = JsonHelper.get(restURL + "/service/repository/process-definition?key=${key}")
        String id = response.get("id")

        //assembling JSONObject for second request
        JSONObject requestBody = new JSONObject()
        requestBody.put("variables", testCase.variables)
        requestBody.put("businessKey", "key-${key}")


        if (!testCase.selfStarting) {
            //second request to start process using id and Json to get the process instance id
            JsonHelper.post(restURL + "/service/repository/process-definition/${id}/start", requestBody)
        }

        WaitTasks.sleep(testCase.delay)

        BPMNTester.setupPathToToolsJarForJavacAntTask(this)
        BPMNTester.compileTest(testSrc, testBin)
        BPMNTester.executeTest(testSrc, testBin, reportPath)
    }


    private Path getFileName() {
        logDir.resolve("..").normalize().resolve("bin").resolve("log" + testCase.number + ".txt")
    }

    public static boolean createInstance(String url, String key) {
        return true;
//
//        try {
//            log.info("Deploying file " + bpmnFile.toAbsolutePath());
//
//            String deploymentUrl = URL + "/service/repository/deployments";
//            log.info("HTTP POST to " + deploymentUrl);
//
//            HttpResponse<JsonNode> jsonResponse = Unirest.post(deploymentUrl).header("type", "multipart/form-data").field("file", bpmnFile.toFile()).asJson();
//
//            log.info("HTTP RESPONSE code: " + jsonResponse.getCode());
//            log.info("HTTP RESPONSE body: " + jsonResponse.getBody());
//
//            return 201 == jsonResponse.getCode();
//
//        } catch (UnirestException e) {
//            throw new RuntimeException("Could not deploy", e);
//        } finally {
//            try {
//                Unirest.shutdown();
//            } catch (IOException e) {
//                log.error("problem during shutdown of unirest lib", e);
//            }
//
//        }

    }

    private static final Logger log = Logger.getLogger(ActivitiTester.class);


}
