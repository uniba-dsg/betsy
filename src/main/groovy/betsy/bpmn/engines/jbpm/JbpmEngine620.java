package betsy.bpmn.engines.jbpm;

import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;

public class JbpmEngine620 extends JbpmEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "jbpm", "6.2.0", LocalDate.of(2015, 3, 9), "Apache-2.0");
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
    protected JbpmApiBasedProcessInstanceOutcomeChecker createProcessOutcomeChecker() {
        return JbpmApiBasedProcessInstanceOutcomeChecker.build();
    }

}
