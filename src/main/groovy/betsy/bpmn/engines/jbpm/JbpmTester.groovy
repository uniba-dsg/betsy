package betsy.bpmn.engines.jbpm

import betsy.bpmn.engines.BPMNTester
import betsy.bpmn.engines.camunda.JsonHelper
import betsy.bpmn.model.BPMNTestCase
import betsy.bpmn.model.BPMNTestCaseVariable
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks
import org.json.JSONObject

import java.nio.file.Path

class JbpmTester {

    BPMNTestCase testCase
    String name
    String deploymentId
    URL baseUrl
    Path reportPath
    Path testBin
    Path testSrc
    Path logDir
    String user = "admin"
    String password = "admin"

    /**
     * Runs a single test
     */
    public void runTest() {
        //make bin dir
        FileTasks.mkdirs(testBin)
        FileTasks.mkdirs(reportPath)

        if (!testCase.selfStarting) {
            //setup variables and start process
            String baseUrl = "http://" + user + ":" + password + "@localhost:8080/jbpm-console/rest/runtime/" + deploymentId + "/process/" + name + "/start";

            Map<String, Object> variables = new HashMap<>()
            for (BPMNTestCaseVariable variable : testCase.variables) {
                variables.put(variable.getName(), variable.getValue());
            }

            StringJoiner joiner = new StringJoiner(",", "?", "")
            for (Map.Entry<String, Object> entry : variables) {
                joiner.add("map_" + entry.getKey() + "=" + entry.getValue());
            }

            String requestUrl = baseUrl + joiner.toString();
            JsonHelper.post(requestUrl, new JSONObject(), 200)

            //look for error end event special case
            WaitTasks.sleep(200)

/*
if (instance.getState() == ProcessInstance.STATE_ABORTED) {
    BPMNTester.appendToFile(getFileName(), "thrownErrorEvent");
}
//TODO read log file for exceptions or statements

//delay for timer intermediate event
WaitTasks.sleep(testCase.delay)

} catch (RuntimeException ignored) {
BPMNTester.appendToFile(getFileName(), "runtimeException");
}
*/
        } else {
//delay for self starting
            WaitTasks.sleep(testCase.delay)
        }

        BPMNTester.setupPathToToolsJarForJavacAntTask(this)
        BPMNTester.compileTest(testSrc, testBin)
        BPMNTester.executeTest(testSrc, testBin, reportPath)
    }

    private Path getFileName() {
        logDir.resolve("log" + testCase.number + ".txt")
    }
}
