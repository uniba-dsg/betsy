package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import org.codehaus.groovy.tools.RootLoader

import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 09:58
 */
class CamundaEngine extends LocalEngine {

    private static final AntBuilder ant = AntUtil.builder()

    @Override
    String getName() {
        "camunda"
    }

    String getCamundaUrl(){
        "http://localhost:8080"
    }

    @Override
    void deploy(BetsyProcess process) {
        new CamundaDeployer(processName: process.name,
                packageFilePath: process.targetPackageFilePath,
                deploymentDirPath: getDeploymentDir(),
                logFilePath: Paths.get("") //TODO
        ).deploy()
    }

    void deployTest(){
        /*new CamundaDeployer(processName: "loan-approval-0.0.1-SNAPSHOT",
                packageFilePath: Paths.get("downloads/loan-approval-0.0.1-SNAPSHOT.war"),
                deploymentDirPath: Paths.get("server/camunda/server/apache-tomcat-7.0.33/webapps/"),
                logFilePath: Paths.get("") //TODO
        ).deploy()*/
        new CamundaDeployer(processName: "Simple BPM",
                packageFilePath: Paths.get("test/camunda/tasks__simple/war/simple.war"),
                deploymentDirPath: Paths.get("server/camunda/server/apache-tomcat-7.0.33/webapps/"),
                logFilePath: Paths.get("server/camunda/server/apache-tomcat-7.0.33/bin/") //TODO
        ).deploy()
    }

    @Override
    void buildArchives(BetsyProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    void buildWar(){

        new CamundaResourcesGenerator().generateWar("Simple BPM", "org.camunda.bpm.simple", "simple")
        /*// needed process information
        String filename = "simple"
        String processName = "Simple BPM"
        String processDir = "downloads/camunda-sequence"
        String groupId = "org.camunda.bpm.simple"
        String key = "simple"

        //directory structure
        String srcDir = "buildBPMNTest"
        String dir = "${srcDir}/${processName}"
        String pomDir = "${dir}/META-INF/maven/${groupId}/${key}"
        String classesDir = "${dir}/WEB-INF/classes"

        //setup infrastructure
        FileTasks.mkdirs(Paths.get(pomDir))
        FileTasks.mkdirs(Paths.get(classesDir))

        //generate pom.properties
        PrintWriter pw = new PrintWriter("${pomDir}/pom.properties", "UTF-8")
        pw.println("version=0.0.1-SNAPSHOT")
        pw.println("groupId=${groupId}")
        pw.println("artifactId=${key}")
        pw.close()

        //copy process specific files
        ant.copy(todir: pomDir){
            fileset(dir: "${processDir}/pom"){ }
        }
        ant.copy(todir: classesDir){
            fileset(dir: "${processDir}/process"){ }
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

        // compile sources
        ant.javac(srcdir: "${processDir}/src", destdir: classesDir, includeantruntime: false) {
            classpath{
                pathelement(location: "downloads/camunda-engine-7.0.0-Final.jar")
                pathelement(location: "downloads/javaee-api-7.0.jar")
            }
        }
        //pack war
        ant.war(destfile: "${srcDir}/${filename}.war", needxmlfile: false){
            fileset(dir: dir){ }
        }*/
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void storeLogs(BetsyProcess process) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void install() {
        new CamundaInstaller(destinationDir: serverPath).install()
    }

    @Override
    void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath, "camunda_startup.bat"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("camunda_startup.sh")))

        /*ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: camundaUrl
        } */
    }

    @Override
    void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("camunda_shutdown.bat")))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(serverPath.resolve("camunda_shutdown.sh")))
    }

    @Override
    boolean isRunning() {
        ant.fail(message: "tomcat for engine ${serverPath} is still running") {
            condition() {
                http url: camundaUrl
            }
        }
    }
}
