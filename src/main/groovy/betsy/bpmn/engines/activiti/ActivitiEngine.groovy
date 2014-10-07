package betsy.bpmn.engines.activiti

import betsy.bpel.engines.tomcat.Tomcat
import betsy.bpel.engines.tomcat.TomcatInstaller;
import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.model.BPMNProcess
import betsy.common.config.Configuration
import betsy.common.tasks.NetworkTasks
import betsy.common.tasks.ZipTasks;

public class ActivitiEngine extends BPMNEngine {

    @Override
    public void buildTest(BPMNProcess process) {

    }

    @Override
    public void testProcess(BPMNProcess process) {

    }

    @Override
    public String getName() {
        return "activiti";
    }

    @Override
    public void deploy(BPMNProcess process) {

    }

    @Override
    public void buildArchives(BPMNProcess process) {

    }

    @Override
    public String getEndpointUrl(BPMNProcess process) {
        return null;
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
        tomcatInstaller.deployWar(getServerPath().resolve("activiti-5.16.3").resolve("wars").resolve("activiti-rest.war"))
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
