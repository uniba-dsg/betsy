package betsy.bpmn.engines.jbpm;

import betsy.common.util.ClasspathHelper;


import java.nio.file.Path;

public class JbpmEngine620 extends JbpmEngine {

    @Override
    public String getName() {
        return "jbpm620";
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
        jbpmInstaller.setDestinationDir(getServerPath());
        jbpmInstaller.setFileName("jbpm-6.2.0.Final-installer-full.zip");
        jbpmInstaller.install();
    }

    @Override
    protected String createProcessHistoryURL(String deploymentId) {
        return getJbpmnUrl()+ "/rest/history/instance/1";
    }

}
