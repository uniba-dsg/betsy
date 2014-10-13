package betsy.bpmn.engines.camunda

import betsy.bpmn.engines.BPMNTester
import betsy.bpmn.model.BPMNTestCase
import betsy.bpmn.model.BPMNTestCaseVariable
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks
import org.json.JSONObject

import java.nio.file.Files
import java.nio.file.Path

class CamundaTester {

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

        CamundaLogFileAnalyzer analyzer = new CamundaLogFileAnalyzer(FileTasks.findFirstMatchInFolder(logDir, "catalina*"));

        Set<String> deploymentErrors = analyzer.getDeploymentErrors();

        if (!deploymentErrors.isEmpty()) {
            for (String deploymentError: deploymentErrors) {
                BPMNTester.appendToFile(getFileName(), deploymentError);
            }
        } else {

            if (!testCase.selfStarting) {
                //first request to get id
                JSONObject response = JsonHelper.get(restURL + "/process-definition?key=${key}", 200)
                String id = response.get("id")

                //assembling JSONObject for second request
                JSONObject requestBody = new JSONObject()
                requestBody.put("variables", mapToArrayWithMaps(testCase.variables))
                requestBody.put("businessKey", "key-${key}")

                //second request to start process using id and Json to get the process instance id
                JsonHelper.post(restURL + "/process-definition/${id}/start?key=${key}", requestBody, 200)
            }

            WaitTasks.sleep(testCase.delay)

            for (String deploymentError: analyzer.getRuntimeErrors()) {
                BPMNTester.appendToFile(getFileName(), deploymentError);
            }
        }


        BPMNTester.setupPathToToolsJarForJavacAntTask(this)
        BPMNTester.compileTest(testSrc, testBin)
        BPMNTester.executeTest(testSrc, testBin, reportPath)
    }

    public static Map<String, Object> mapToArrayWithMaps(List<BPMNTestCaseVariable> variables) {
        Map<String, Object> map = new HashMap<>();

        for (BPMNTestCaseVariable entry : variables) {
            Map<String, Object> submap = new HashMap<>();
            submap.put("value", entry.getValue());
            submap.put("type", entry.getType());
            map.put(entry.getName(), submap);
        }

        return map;
    }


    private Path getFileName() {
        logDir.resolve("..").normalize().resolve("bin").resolve("log" + testCase.number + ".txt")
    }


}
