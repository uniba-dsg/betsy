package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;
import java.time.LocalDate;

import betsy.common.model.engine.EngineExtended;
import pebl.ProcessLanguage;

public class JbpmEngine640 extends JbpmEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "jbpm", "6.4.0", LocalDate.of(2016, 4, 19), "Apache-2.0");
    }

    @Override
    public String getJbossName() {
        return "wildfly-8.2.1.Final";
    }

    @Override
    public Path getLogFileForShutdownAnalysis() {
        return getServerLog();
    }

    @Override
    public void install() {
        JbpmInstaller jbpmInstaller = new JbpmInstaller();
        jbpmInstaller.setDestinationDir(getServerPath());
        jbpmInstaller.setFileName("jbpm-6.4.0.Final-installer-full.zip");
        jbpmInstaller.install();
    }

    @Override
    protected JbpmApiBasedProcessInstanceOutcomeChecker createProcessOutcomeChecker(String name) {
        String url = getJbpmnUrl() + "/rest/history/instance/1";
        String deployCheckUrl = getJbpmnUrl() + "/rest/deployment/" + getDeploymentId(name) + "/processes";
        return new Jbpm640MixedProcessInstanceOutcomeChecker(url, deployCheckUrl, getServerLog());
    }

}
