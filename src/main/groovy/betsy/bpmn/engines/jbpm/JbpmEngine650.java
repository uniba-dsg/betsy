package betsy.bpmn.engines.jbpm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;
import pebl.ProcessLanguage;

public class JbpmEngine650 extends JbpmEngine {

    private static final Logger LOGGER = Logger.getLogger(JbpmEngine650.class);

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
    public void startup() {

        FileTasks.replaceTokenInFile(getJbpmInstallerPath().resolve("build.xml"),"<env key=\"JAVA_OPTS\" value=\"-XX:MaxPermSize=256m -Xms256m -Xmx512m\" />", "<env key=\"JAVA_OPTS\" value=\"-XX:MaxPermSize=256m -Xms512m -Xmx2048m -XX:-UseGCOverheadLimit\" />");

        ConsoleTasks.setupAnt(getAntPath());

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant start.demo.noeclipse"));;
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant").values("start.demo.noeclipse"));

        //waiting for jbpm-console for deployment and instantiating
        TimeoutRepository.getTimeout("Jbpm.startup").waitForSubstringInFile(getServerLog(), " WFLYSRV0010: Deployed \"jbpm-console.war\"");
    }

    @Override
    public void shutdown() {
        Path jbpmInstallerPath = getJbpmInstallerPath();
        if (!Files.exists(jbpmInstallerPath)) {
            // if it is not installed, we cannot shutdown
            return;
        }

        ConsoleTasks.setupAnt(getAntPath());

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmInstallerPath, getAntPath().toAbsolutePath() + "/ant -q stop.demo"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmInstallerPath, getAntPath().toAbsolutePath() + "/ant").values("-q", "stop.demo"));

        if (FileTasks.hasNoFile(getLogFileForShutdownAnalysis())) {
            LOGGER.info("Could not shutdown, because " + getLogFileForShutdownAnalysis() + " does not exist. this indicates that the engine was never started");
            return;
        }

        try {
            //waiting for shutdown completion using log files; e.g. 2016-11-25 11:45:34,702 INFO  [org.jboss.as] (MSC service thread 1-3) WFLYSRV0050: WildFly Full 10.0.0.Final (WildFly Core 2.0.10.Final) stopped in 2820ms
            TimeoutRepository.getTimeout("Jbpm.shutdown").waitForSubstringInFile(getLogFileForShutdownAnalysis(), "WFLYSRV0050");

            // clean up data (with db and config files in the users home directory)
            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmInstallerPath, getAntPath().toAbsolutePath() + "/ant -q clean.demo"));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmInstallerPath, getAntPath().toAbsolutePath() + "/ant").values("-q").values("clean.demo"));
        } catch (IllegalStateException ex) {
            //swallow
        }
    }

    @Override
    protected JbpmApiBasedProcessInstanceOutcomeChecker createProcessOutcomeChecker(String name) {
        String url = getJbpmnUrl() + "/rest/history/instance/1";
        String deployCheckUrl = getJbpmnUrl() + "/rest/deployment/" + getDeploymentId(name);
        return new JbpmApiBasedProcessInstanceOutcomeChecker(url, deployCheckUrl);
    }

}
