package betsy.bpmn.engines.camunda;

import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class Camunda730Engine extends Camunda710Engine {

    @Override
    public String getName() {
        return "camunda730";
    }

    @Override
    public String getTomcatName() {
        return "apache-tomcat-7.0.62";
    }

    @Override
    public void install() {
        CamundaInstaller camundaInstaller = new CamundaInstaller();
        camundaInstaller.setDestinationDir(getServerPath());
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.3.0.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();
    }
}
