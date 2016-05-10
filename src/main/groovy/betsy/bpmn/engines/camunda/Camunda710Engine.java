package betsy.bpmn.engines.camunda;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.WaitTasks;

import java.time.LocalDate;
import java.util.Optional;

public class Camunda710Engine extends CamundaEngine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "camunda", "7.1.0", LocalDate.of(2014, 3, 31), "Apache-2.0");
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
        WaitTasks.waitForAvailabilityOfUrl(30_000, 500, getCamundaUrl());
    }

}
