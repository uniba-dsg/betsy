package betsy.bpmn.engines.jbpm;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;

import java.time.LocalDate;

public class JbpmEngine640 extends JbpmEngine610 {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "jbpm", "6.4.0", LocalDate.of(2016, 4, 19), "Apache-2.0");
    }

    @Override
    public String getJbossName() {
        return "wildfly-8.2.1.Final";
    }

    @Override
    public String getLogFileNameForShutdownAnalysis() {
        return "server.log";
    }

    @Override
    public void install() {
        JbpmInstaller jbpmInstaller = new JbpmInstaller();
        jbpmInstaller.setDestinationDir(getServerPath());
        jbpmInstaller.setFileName("jbpm-6.4.0.Final-installer-full.zip");
        jbpmInstaller.install();
    }

    @Override
    protected String createProcessDeploymentURL(String deploymentId) {
        return getJbpmnUrl() + "/rest/deployment/" + deploymentId + "/processes";
    }
}
