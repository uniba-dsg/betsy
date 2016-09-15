package betsy.bpmn.engines.jbpm;

import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;

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
    protected JbpmApiBasedProcessInstanceOutcomeChecker createProcessOutcomeChecker() {
        return JbpmApiBasedProcessInstanceOutcomeChecker.build();
    }

}
