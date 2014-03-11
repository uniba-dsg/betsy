package betsy.data.engines.jbpm

import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.services.client.api.RemoteRestRuntimeFactory

/**
 * Created with IntelliJ IDEA.
 * User: stavorndran
 * Date: 07.03.14
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
class JbpmTester {

    public void runTest(){
        // start process
        //String deploymentId = "org.jbpm:Evaluation:1.0"
        String deploymentId = "testo:testo:1.0"
        URL baseUrl = new URL("http://localhost:8080/jbpm-console/")
        String user = "admin"
        String password = "admin"
        RemoteRestRuntimeFactory factory = new RemoteRestRuntimeFactory(deploymentId, baseUrl, user, password)
        RuntimeEngine  remoteEngine = factory.newRuntimeEngine()
        KieSession kSession = remoteEngine.getKieSession()
        ProcessInstance instance = kSession.startProcess("testo")
    }
}
