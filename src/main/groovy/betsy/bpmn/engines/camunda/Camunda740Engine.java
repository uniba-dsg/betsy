package betsy.bpmn.engines.camunda;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.Engine;

public class Camunda740Engine extends Camunda710Engine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPMN, "camunda", "7.4.0");
    }

    @Override
    public String getTomcatName() {
        return "apache-tomcat-8.0.24";
    }

    @Override
    public void install() {
        CamundaInstaller camundaInstaller = new CamundaInstaller();
        camundaInstaller.setDestinationDir(getServerPath());
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.4.0.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();
    }
}
