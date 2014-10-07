package betsy.bpmn.engines.jbpm

import betsy.bpmn.engines.BPMNTester
import betsy.bpmn.model.BPMNTestCase
import betsy.common.tasks.FileTasks
import betsy.common.tasks.WaitTasks
import org.json.JSONObject
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.services.client.api.RemoteRestRuntimeFactory

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

        //connect to remote
        RemoteRestRuntimeFactory factory = new RemoteRestRuntimeFactory(deploymentId, baseUrl, user, password)
        RuntimeEngine remoteEngine = factory.newRuntimeEngine()
        KieSession kSession = remoteEngine.getKieSession()

        if (!testCase.selfStarting) {
            //setup variables and start process
            try {
                Map<String, Object> variables = new HashMap<>()
                for (String key : testCase.variables.keySet()) {
                    variables.put(key, ((JSONObject) testCase.variables.get(key)).get("value"))
                }
                ProcessInstance instance = kSession.startProcess(name, variables)
                //look for error end event special case
                WaitTasks.sleep(200)
                if (instance.getState() == ProcessInstance.STATE_ABORTED) {
                    BPMNTester.writeToLog(getFileName(), "thrownErrorEvent");
                }
                //delay for timer intermediate event
                if (testCase.delay != 0) {
                    WaitTasks.sleep(testCase.delay)
                }
            } catch (RuntimeException ignored) {
                BPMNTester.writeToLog(getFileName(), "runtimeException");
            }
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
