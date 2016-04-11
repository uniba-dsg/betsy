package betsy.bpmn.engines.jbpm;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.Engine;

public class JbpmEngine630 extends JbpmEngine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPMN, "jbpm", "6.3.0");
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
        jbpmInstaller.setFileName("jbpm-6.3.0.Final-installer-full.zip");
        jbpmInstaller.install();
    }

    @Override
    protected String createProcessHistoryURL(String deploymentId) {
        return getJbpmnUrl() + "/rest/history/instance/1";
    }

}
