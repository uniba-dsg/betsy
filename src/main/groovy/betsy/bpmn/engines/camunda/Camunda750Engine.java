package betsy.bpmn.engines.camunda;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;

import java.time.LocalDate;

public class Camunda750Engine extends Camunda710Engine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "camunda", "7.5.0", LocalDate.of(2016, 05, 31), "Apache-2.0");
    }

    @Override
    public String getTomcatName() {
        return "apache-tomcat-8.0.24";
    }

    @Override
    public void install() {
        CamundaInstaller camundaInstaller = new CamundaInstaller();
        camundaInstaller.setDestinationDir(getServerPath());
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.5.0.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();
    }
}
