package betsy.data.engines.jbpm

import ant.tasks.AntUtil
import betsy.data.BPMNTestCase
import betsy.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader
import org.json.JSONObject
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
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

    public void runTest(){
        //make bin dir
        FileTasks.mkdirs(testBin)
        FileTasks.mkdirs(reportPath)

        //connect to remote
        RemoteRestRuntimeFactory factory = new RemoteRestRuntimeFactory(deploymentId, baseUrl, user, password)
        RuntimeEngine  remoteEngine = factory.newRuntimeEngine()
        KieSession kSession = remoteEngine.getKieSession()

        //setup variables and start process
        try {
            if(testCase.variables != null){
                Map<String, Object> variables = new HashMap<>()
                for(String key : testCase.variables.keySet()){
                    variables.put(key, ((JSONObject)testCase.variables.get(key)).get("value"))
                }
                kSession.startProcess(name, variables)
            }else{
                kSession.startProcess(name)
            }
        }catch (RuntimeException e){
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter("${logDir}/log" + testCase.number + ".txt", true));
                bw.append("runtimeException");
                bw.close();
            }catch(IOException ioe){}
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
