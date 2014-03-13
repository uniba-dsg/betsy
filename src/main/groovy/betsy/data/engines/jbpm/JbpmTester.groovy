package betsy.data.engines.jbpm

import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.services.client.api.RemoteRestRuntimeFactory

class JbpmTester {

    String name
    String deploymentId
    URL baseUrl
    String user = "admin"
    String password = "admin"

    public void runTest(){
        // start process
        //String deploymentId = "org.jbpm:Evaluation:1.0"

        RemoteRestRuntimeFactory factory = new RemoteRestRuntimeFactory(deploymentId, baseUrl, user, password)
        RuntimeEngine  remoteEngine = factory.newRuntimeEngine()
        KieSession kSession = remoteEngine.getKieSession()
        ProcessInstance instance = kSession.startProcess(name)
    }
}
