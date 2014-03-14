package betsy.data.engines.jbpm

import ant.tasks.AntUtil
import betsy.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader
import org.kie.api.runtime.KieSession
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.api.runtime.process.ProcessInstance
import org.kie.services.client.api.RemoteRestRuntimeFactory

import java.nio.file.Path

class JbpmTester {

    private static final AntBuilder ant = AntUtil.builder()

    String name
    String deploymentId
    URL baseUrl
    Path reportPath
    Path testBin
    Path testSrc
    String user = "admin"
    String password = "admin"

    public void runTest(){
        //make bin dir
        FileTasks.mkdirs(testBin)
        FileTasks.mkdirs(reportPath)

        // start process
        //String deploymentId = "org.jbpm:Evaluation:1.0"

        RemoteRestRuntimeFactory factory = new RemoteRestRuntimeFactory(deploymentId, baseUrl, user, password)
        RuntimeEngine  remoteEngine = factory.newRuntimeEngine()
        KieSession kSession = remoteEngine.getKieSession()
        //TODO start process with variables
        kSession.startProcess(name)

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
