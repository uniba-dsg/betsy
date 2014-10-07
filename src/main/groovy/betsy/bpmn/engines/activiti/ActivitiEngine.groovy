package betsy.bpmn.engines.activiti

import betsy.bpel.engines.tomcat.Tomcat
import betsy.bpel.engines.tomcat.TomcatInstaller;
import betsy.bpmn.engines.BPMNEngine
import betsy.bpmn.engines.camunda.CamundaResourcesGenerator
import betsy.bpmn.engines.camunda.CamundaTester;
import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestBuilder
import betsy.bpmn.model.BPMNTestCase
import betsy.bpmn.reporting.BPMNTestcaseMerger
import betsy.common.config.Configuration
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks
import betsy.common.tasks.WaitTasks
import betsy.common.tasks.ZipTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Path;

public class ActivitiEngine extends BPMNEngine {

    @Override
    void testProcess(BPMNProcess process){
        
    }

    @Override
    public String getName() {
        return "activiti";
    }

    @Override
    public void deploy(BPMNProcess process) {

    }

    @Override
    void buildArchives(BPMNProcess process) {

    }

    @Override
    void buildTest(BPMNProcess process){

    }

    @Override
    String getEndpointUrl(BPMNProcess process) {
        "http://kermit:kermit@localhost:8080/activiti-rest/service/repository/"
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
