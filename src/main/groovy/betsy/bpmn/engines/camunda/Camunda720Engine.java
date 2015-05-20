package betsy.bpmn.engines.camunda;

import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class Camunda720Engine extends CamundaEngine {

    @Override
    public String getName() {
        return "camunda720";
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
        camundaInstaller.setFileName("camunda-bpm-tomcat-7.2.0.zip");
        camundaInstaller.setTomcatName(getTomcatName());
        camundaInstaller.install();
    }

    @Override
    public void startup() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), "camunda_startup.bat"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("camunda_startup.sh")));
        WaitTasks.waitForAvailabilityOfUrl(30_000, 500, getCamundaUrl());
    }

}
