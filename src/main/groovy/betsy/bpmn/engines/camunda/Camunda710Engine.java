package betsy.bpmn.engines.camunda;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.util.Optional;

public class Camunda710Engine extends CamundaEngine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPMN, "camunda", "7.1.0");
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
