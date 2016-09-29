package betsy.bpmn.engines.camunda;

import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;

public class Camunda730Engine extends Camunda720Engine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "camunda", "7.3.0", LocalDate.of(2015, 5, 29), "Apache-2.0");
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
