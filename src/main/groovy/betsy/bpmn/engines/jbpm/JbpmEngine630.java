package betsy.bpmn.engines.jbpm;

import java.time.LocalDate;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;

public class JbpmEngine630 extends JbpmEngine610 {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "jbpm", "6.3.0", LocalDate.of(2015, 9, 28), "Apache-2.0");
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

}
