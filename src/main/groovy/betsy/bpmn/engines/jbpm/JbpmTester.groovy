package betsy.bpmn.engines.jbpm

import ant.tasks.AntUtil
import betsy.bpmn.model.BPMNTestCase
import betsy.tasks.FileTasks
import betsy.tasks.WaitTasks
import org.codehaus.groovy.tools.RootLoader
import org.json.JSONObject
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.services.client.api.RemoteRestRuntimeFactory

import java.nio.file.Path

class JbpmTester {

    private static final AntBuilder ant = AntUtil.builder()

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
    public void runTest(){
        //make bin dir
        FileTasks.mkdirs(testBin)
        FileTasks.mkdirs(reportPath)

        //connect to remote
        RemoteRestRuntimeFactory factory = new RemoteRestRuntimeFactory(deploymentId, baseUrl, user, password)
        RuntimeEngine  remoteEngine = factory.newRuntimeEngine()
        KieSession kSession = remoteEngine.getKieSession()

        if(!testCase.selfStarting){
            //setup variables and start process
            try {
                Map<String, Object> variables = new HashMap<>()
                for(String key : testCase.variables.keySet()){
                    variables.put(key, ((JSONObject)testCase.variables.get(key)).get("value"))
                }
                ProcessInstance instance = kSession.startProcess(name, variables)
                //look for error end event special case
                WaitTasks.sleep(200)
                if(instance.getState() == ProcessInstance.STATE_ABORTED){
                    try{
                        BufferedWriter bw = new BufferedWriter(new FileWriter("${logDir}/log" + testCase.number + ".txt", true));
                        bw.append("thrownErrorEvent");
                        bw.newLine()
                        bw.close();
                    }catch(IOException ioe){}
                }
                //delay for timer intermediate event
                if(testCase.delay != 0){
                    WaitTasks.sleep(testCase.delay)
                }
            }catch (RuntimeException e){
                try{
                    BufferedWriter bw = new BufferedWriter(new FileWriter("${logDir}/log" + testCase.number + ".txt", true));
                    bw.append("runtimeException");
                    bw.close();
                }catch(IOException ioe){}
            }
        }else{
            //delay for self starting
            WaitTasks.sleep(testCase.delay)
        }

        //setup path to 'tools.jar' for the javac ant task
        String javaHome = System.getProperty("java.home")
        if(javaHome.endsWith("jre")){
            javaHome = javaHome.substring(0, javaHome.length() - 4)
        }
        RootLoader rl = (RootLoader) this.class.classLoader.getRootLoader()
        if(rl == null){
            Thread.currentThread().getContextClassLoader().addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }else{
            rl.addURL(new URL("file:///${javaHome}/lib/tools.jar"))
        }

        String systemClasspath = System.getProperty('java.class.path')

        // compile test sources
        ant.javac(srcdir: testSrc, destdir: testBin, includeantruntime: false) {
            classpath{
                pathelement(path: systemClasspath)
            }
        }

        //runt test
        ant.junit(printsummary: "on", fork: "true", haltonfailure: "no"){
            classpath{
                pathelement(path: systemClasspath)
                pathelement(location: testBin)
            }
            formatter(type: "xml")
            batchtest(todir: reportPath){
                fileset(dir: testSrc){
                    include(name: "**/*.java")
                }
            }
        }
    }
}
