package betsy.bpmn.engines.activiti

import betsy.bpel.engines.tomcat.Tomcat
import betsy.bpel.engines.tomcat.TomcatInstaller;
import betsy.bpmn.engines.BPMNEngine
import betsy.bpmn.model.BPMNProcess
import betsy.common.config.Configuration
import betsy.common.tasks.NetworkTasks
import betsy.common.tasks.ZipTasks
import betsy.common.util.ClasspathHelper
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import org.apache.log4j.Logger

import java.nio.file.Path;

public class ActivitiEngine extends BPMNEngine {

    private static final Logger log = Logger.getLogger(ActivitiEngine.class);

    public static final String URL = "http://kermit:kermit@localhost:8080/activiti-rest"

    @Override
    void testProcess(BPMNProcess process){

    }

    @Override
    public String getName() {
        return "activiti";
    }

    @Override
    public void deploy(BPMNProcess process) {
        deployBpmnProcess(getTargetProcessBpmnFile(process))
    }

    public static boolean deployBpmnProcess(Path bpmnFile) {
        try {
            log.info("Deploying file " + bpmnFile.toAbsolutePath());

            String deploymentUrl = URL + "/service/repository/deployments";
            log.info("HTTP POST to " + deploymentUrl);

            HttpResponse<JsonNode> jsonResponse = Unirest.post(deploymentUrl)
                    .header("type", "multipart/form-data")
                    .field("file", bpmnFile.toFile())
                    .asJson();

            log.info("HTTP RESPONSE code: " + jsonResponse.getCode());
            log.info("HTTP RESPONSE body: " + jsonResponse.getBody());

            return 201 == jsonResponse.getCode();

        } catch (UnirestException e) {
            throw new RuntimeException("Could not deploy", e);
        } finally {
            try {
                Unirest.shutdown();
            } catch (IOException e) {
                log.error("problem during shutdown of unirest lib", e);
            }
        }
    }

    @Override
    Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/camunda");
    }

    @Override
    void buildArchives(BPMNProcess process) {
        ant.xslt(in: process.resourcePath.resolve("${process.name}.bpmn"),
                out: getTargetProcessFolder(process).resolve("${process.name}.bpmn-temp"),
                style: xsltPath.resolve("../scriptTask.xsl"))
        ant.xslt(in: getTargetProcessFolder(process).resolve("${process.name}.bpmn-temp"),
                out: getTargetProcessBpmnFile(process),
                style: xsltPath.resolve("camunda.xsl"))
        ant.delete(file: getTargetProcessFolder(process).resolve("${process.name}.bpmn-temp"))
    }

    private Path getTargetProcessBpmnFile(BPMNProcess process) {
        getTargetProcessFolder(process).resolve(process.getName() + ".bpmn")
    }

    private Path getTargetProcessFolder(BPMNProcess process) {
        process.targetPath.resolve("process")
    }

    @Override
    void buildTest(BPMNProcess process){

    }

    @Override
    String getEndpointUrl(BPMNProcess process) {
        URL + "/service/repository/"
    }

    @Override
    public void storeLogs(BPMNProcess process) {

    }

    @Override
    public void install() {
        // install tomcat
        TomcatInstaller tomcatInstaller = new TomcatInstaller()
        tomcatInstaller.setDestinationDir(getServerPath())
        tomcatInstaller.install()

        // unzip activiti
        String filename = "activiti-5.16.3.zip"
        NetworkTasks.downloadFile(new URL("https://github.com/Activiti/Activiti/releases/download/activiti-5.16.3/" + filename), Configuration.getDownloadsDir());
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(filename), getServerPath());

        // deploy
        getTomcat().deployWar(getServerPath().resolve("activiti-5.16.3").resolve("wars").resolve("activiti-rest.war"))
    }

    Tomcat getTomcat() {
        new Tomcat(engineDir: serverPath)
    }

    @Override
    void startup() {
        tomcat.startup()
    }

    @Override
    void shutdown() {
        tomcat.shutdown()
    }

    @Override
    public boolean isRunning() {
        tomcat.checkIfIsRunning()
    }
}
