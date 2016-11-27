package betsy.bpmn.engines.camunda;

import java.time.LocalDate;
import java.util.Optional;

import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import pebl.ProcessLanguage;

public class Camunda710Engine extends CamundaEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "camunda", "7.1.0", LocalDate.of(2014, 3, 31), "Apache-2.0");
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
        camundaInstaller.setGroovyFile(Optional.of("groovy-all-2.2.0.jar"));
        camundaInstaller.install();
    }

    @Override
    public void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), "camunda_startup.bat"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("camunda_startup.sh")));
        TimeoutRepository.getTimeout("Camunda710.startup").waitForAvailabilityOfUrl(getCamundaUrl());
    }

}
