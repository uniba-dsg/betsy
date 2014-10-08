package betsy.bpmn.engines.camunda

import betsy.bpmn.engines.BPMNTester
import betsy.bpmn.model.BPMNTestCase
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks
import groovy.io.FileType
import org.json.JSONObject

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

        //look up log for deployment error caused by unsupported activity type
        String unsupportedMessage = null
        File dir = new File(logDir.toString())
        dir.eachFile(FileType.FILES) { file ->
            if (file.name.contains("catalina")) {
                BufferedReader br = new BufferedReader(new FileReader(file))
                br.eachLine(150) { line ->
                    if (line.contains("Ignoring unsupported activity type")) {
                        unsupportedMessage = line
                    }
                    if (line.startsWith("org.camunda.bpm.engine.ProcessEngineException")) {
                        unsupportedMessage = line
                    }
                    //TODO what happens in all the other cases?
                }
            }
        }
        if (unsupportedMessage != null) {
            BPMNTester.appendToFile(getFileName(), unsupportedMessage);
        } else {
            //first request to get id
            JSONObject response = JsonHelper.get(restURL + "/process-definition?key=${key}")
            String id = response.get("id")

            //assembling JSONObject for second request
            JSONObject requestBody = new JSONObject()
            requestBody.put("variables", testCase.variables)
            requestBody.put("businessKey", "key-${key}")


            if (testCase.selfStarting) {
                //just wait until process starts itself
                WaitTasks.sleep(testCase.delay);
            } else {
                //second request to start process using id and Json to get the process instance id
                JsonHelper.post(restURL + "/process-definition/${id}/start", requestBody)
                if (testCase.delay != 0) {
                    WaitTasks.sleep(testCase.delay)
                }
            }
        }


        BPMNTester.setupPathToToolsJarForJavacAntTask(this)
        BPMNTester.compileTest(testSrc, testBin)

        //look up log for runtime exception if process could be started
        if (unsupportedMessage == null) {
            boolean runtimeExceptionFound = false
            dir.eachFile(FileType.FILES) { file ->
                if (file.name.contains("catalina")) {
                    BufferedReader br = new BufferedReader(new FileReader(file))
                    br.eachLine(170) { line ->
                        if (line.startsWith("org.camunda.bpm.engine.ProcessEngineException")) {
                            runtimeExceptionFound = true
                        }
                        //special case for error end event
                        if (line.contains("EndEvent_2 throws error event with errorCode 'ERR-1'")) {
                            BPMNTester.appendToFile(getFileName(),"thrownErrorEvent")
                        }
                    }
                }
            }
            if (runtimeExceptionFound) {
                BPMNTester.appendToFile(getFileName(), "runtimeException");
            }
        }

        BPMNTester.executeTest(testSrc, testBin, reportPath)
    }


    private Path getFileName() {
        logDir.resolve("..").normalize().resolve("bin").resolve("log" + testCase.number + ".txt")
    }


}
