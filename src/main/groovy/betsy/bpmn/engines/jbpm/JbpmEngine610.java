package betsy.bpmn.engines.jbpm;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class JbpmEngine610 extends JbpmEngine {

    @Override
    public String getName() {
        return "jbpm610";
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/" + super.getName());
    }

    @Override
    public String getJbossName() {
        return "wildfly-8.1.0.Final";
    }

    @Override
    public String getLogFileNameForShutdownAnalysis() {
        return "server.log";
    }

    @Override
    public void install() {
        JbpmInstaller jbpmInstaller = new JbpmInstaller();
        jbpmInstaller.setDestinationDir(getServerPath().getParent());
        jbpmInstaller.setFileName("jbpm-6.1.0.Final-installer-full.zip");
        jbpmInstaller.install();

    }

    @Override
    protected String createProcessHistoryURL(String deploymentId) {
        return getJbpmnUrl()+ "/rest/history/instance/1";
    }
}
