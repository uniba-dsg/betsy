package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;
import java.time.LocalDate;

import betsy.common.model.engine.EngineExtended;
import pebl.ProcessLanguage;

public class JbpmEngine650 extends JbpmEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "jbpm", "6.5.0", LocalDate.of(2016, 10, 25), "Apache-2.0");
    }

    @Override
    public String getJbossName() {
        return "wildfly-10.0.0.Final";
    }

    @Override
    public Path getLogFileForShutdownAnalysis() {
        return getServerLog();
    }

    @Override
    public void install() {
        JbpmInstaller jbpmInstaller = new JbpmInstaller();
        jbpmInstaller.setDestinationDir(getServerPath());
        jbpmInstaller.setFileName("jbpm-6.5.0.Final-installer-full.zip");
        jbpmInstaller.install();
    }

    @Override
    protected JbpmApiBasedProcessInstanceOutcomeChecker createProcessOutcomeChecker(String name) {
        String url = getJbpmnUrl() + "/rest/history/instance/1";
        String deployCheckUrl = getJbpmnUrl() + "/rest/deployment/" + getDeploymentId(name);
        return new JbpmApiBasedProcessInstanceOutcomeChecker(url, deployCheckUrl);
    }

}
