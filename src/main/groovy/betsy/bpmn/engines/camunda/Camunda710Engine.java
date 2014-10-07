package betsy.bpmn.engines.camunda;

import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class Camunda710Engine extends CamundaEngine {

    @Override
    public String getName() {
        return "camunda710";
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/" + super.getName());
    }

    @Override
    public String getTomcatName() {
        return "apache-tomcat-7.0.50";
    }

    @Override
    public void install() {
        CamundaInstaller camundaInstaller = new CamundaInstaller();
        camundaInstaller.setDestinationDir(getServerPath());
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.1.0-Final.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();
    }

}
