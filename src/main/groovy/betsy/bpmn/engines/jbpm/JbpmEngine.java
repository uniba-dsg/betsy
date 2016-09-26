package betsy.bpmn.engines.jbpm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.BPMNTestcaseMerger;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.common.config.Configuration;
import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.URLTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.tasks.ZipTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;
import pebl.test.TestCase;

public class JbpmEngine extends AbstractBPMNEngine {

    private static final Logger LOGGER = Logger.getLogger(JbpmEngine.class);

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/jbpm");
    }

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "jbpm", "6.0.1", LocalDate.of(2014, 5, 14), "Apache-2.0");
    }

    public Path getJbpmInstallerPath() {
        return getServerPath().resolve("jbpm-installer");
    }

    public String getJbpmnUrl() {
        return "http://localhost:8080/jbpm-console";
    }

    public String getJbossName() {
        return "jboss-as-7.1.1.Final";
    }

    public String getLogFileNameForShutdownAnalysis() {
        return "boot.log";
    }

    public Path getJbossStandaloneDir() {
        return getJbpmInstallerPath().resolve(getJbossName()).resolve("standalone");
    }

    public Path getAntPath() {
        return Configuration.getAntHome().resolve("bin");
    }

    @Override
    public void deploy(String name, Path path) {
        // maven deployment for pushing it to the local maven repository (jbpm-console will fetch it from there)
        final Path mvnPath = Configuration.getMavenHome().resolve("bin");

        Path installPath = path.getParent().resolve("install");
        ZipTasks.unzip(path, installPath);

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(installPath, mvnPath.toAbsolutePath() + "/mvn -q clean install"));
        ConsoleTasks.setupMvn(mvnPath);
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(installPath, mvnPath.toAbsolutePath() + "/mvn").values("-q", "clean", "install"));

        //wait for maven to deploy
        WaitTasks.sleep(TimeoutRepository.getTimeout("Jbpm.deploy.maven").getTimeoutInMs());

        String deploymentId = getDeploymentId(name);
        JbpmDeployer deployer = new JbpmDeployer(getJbpmnUrl(), deploymentId);
        deployer.deploy();

        TimeoutRepository.getTimeout("Jbpm.deploy").waitFor(deployer::isDeploymentFinished);
    }

    @Override
    public Path buildArchives(final BPMNProcess process) {
        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"),
                process.getProcess(),
                process.getTargetPath().resolve("project/src/main/resources/" + process.getName() + ".bpmn2-temp"),
                "processName", process.getName());
        XSLTTasks.transform(getXsltPath().resolve("jbpm.xsl"), process.getTargetPath().resolve("project/src/main/resources/" + process.getName() + ".bpmn2-temp"), process.getTargetPath().resolve("project/src/main/resources/" + process.getName() + ".bpmn2"));
        FileTasks.deleteFile(process.getTargetPath().resolve("war/WEB-INF/classes/" + process.getName() + ".bpmn2-temp"));

        JbpmResourcesGenerator generator = new JbpmResourcesGenerator();

        generator.setJbpmSrcDir(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/jbpm"));
        generator.setDestDir(process.getTargetPath().resolve("project"));
        generator.setProcessName(process.getName());
        generator.setGroupId("de.uniba.dsg");
        generator.setVersion("1.0");
        generator.generateProject();

        Path zipFile = process.getTargetPackagePath().resolve(process.getName() + ".zip");
        ZipTasks.zipFolder(zipFile, process.getTargetPath().resolve("project"));
        return zipFile;
    }

    @Override
    public String getEndpointUrl(String name) {
        return "http://localhost:8080/jbpm-console/";
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getJbossLogDir()));
        result.addAll(FileTasks.findAllInFolder(getJbpmInstallerPath(), "log*.txt"));

        return result;
    }


    @Override
    public void install() {
        JbpmInstaller installer = new JbpmInstaller();
        installer.setDestinationDir(getServerPath());
        installer.install();
    }

    @Override
    public void startup() {
        Path pathToJava7 = Configuration.getJava7Home();

        FileTasks.replaceTokenInFile(getJbpmInstallerPath().resolve("build.xml"),"<env key=\"JAVA_OPTS\" value=\"-XX:MaxPermSize=256m -Xms256m -Xmx512m\" />", "<env key=\"JAVA_OPTS\" value=\"-XX:MaxPermSize=256m -Xms512m -Xmx2048m -XX:-UseGCOverheadLimit\" />");

        ConsoleTasks.setupAnt(getAntPath());

        Map<String, String> map = new LinkedHashMap<>(1);
        map.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant -q start.demo.noeclipse"), map);
        Map<String, String> map1 = new LinkedHashMap<>(1);
        map1.put("JAVA_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getJbpmInstallerPath(), getAntPath().toAbsolutePath() + "/ant").values("-q", "start.demo.noeclipse"), map1);

        //waiting for jbpm-console for deployment and instantiating
        TimeoutRepository.getTimeout("Jbpm.startup").waitForSubstringInFile(getJbossLogDir().resolve("server.log"), "JBAS018559: Deployed \"jbpm-console.war\"");
    }

    private Path getJbossLogDir() {
        return getJbossStandaloneDir().resolve("log");
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

        if (FileTasks.hasNoFile(getJbossLogDir().resolve(getLogFileNameForShutdownAnalysis()))) {
            LOGGER.info("Could not shutdown, because " + getLogFileNameForShutdownAnalysis() + " does not exist. this indicates that the engine was never started");
            return;
        }

        try {
            //waiting for shutdown completion using log files; e.g. "12:42:36,345 INFO  [org.jboss.as] JBAS015950: JBoss AS 7.1.1.Final "Brontes" stopped in 31957ms"
            TimeoutRepository.getTimeout("Jbpm.shutdown").waitForSubstringInFile(getJbossLogDir().resolve(getLogFileNameForShutdownAnalysis()), "JBAS015950");

            // clean up data (with db and config files in the users home directory)
            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmInstallerPath, getAntPath().toAbsolutePath() + "/ant -q clean.demo"));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(jbpmInstallerPath, getAntPath().toAbsolutePath() + "/ant").values("-q").values("clean.demo"));
        } catch (IllegalStateException ex) {
            //swallow
        }
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(getJbpmnUrl());
    }

    @Override
    public void testProcess(final BPMNProcess process) {
        for (TestCase testCase : process.getTestCases()) {
            BPMNTester bpmnTester = new BPMNTester();
            int testCaseNumber = testCase.getNumber();
            bpmnTester.setSource(process.getTargetTestSrcPathWithCase(testCaseNumber));
            bpmnTester.setTarget(process.getTargetTestBinPathWithCase(testCaseNumber));
            bpmnTester.setReportPath(process.getTargetReportsPathWithCase(testCaseNumber));

            new JbpmTester(
                    process,
                    testCase,
                    bpmnTester,
                    createProcessOutcomeChecker(process.getName()),
                    getInstanceLogFile(process.getName(), testCaseNumber),
                    getJbossLogDir().resolve("server.log")
            ).runTest();
        }

        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    private Path getInstanceLogFile(String processName, int testCaseNumber) {
        return getJbpmInstallerPath().resolve("log-" + processName + "-" + testCaseNumber + ".txt");
    }

    @Override
    public BPMNProcessStarter getProcessStarter() {
        return new JbpmProcessStarter();
    }

    @Override
    public Path getLogForInstance(String processName, String instanceId) {
        return getInstanceLogFile(processName, Integer.parseInt(instanceId));
    }

    protected static String getDeploymentId(String name) {
        return "de.uniba.dsg" + ":" + name + ":" + "1.0";
    }

    @Override
    public boolean isDeployed(QName process) {
        JbpmApiBasedProcessInstanceOutcomeChecker checker = createProcessOutcomeChecker(process.getLocalPart());
        return checker.isProcessDeployed();
    }

    @Override
    public void undeploy(QName process) {
        String deploymentId = getDeploymentId(process.getLocalPart());
        JbpmDeployer deployer = new JbpmDeployer(getJbpmnUrl(), deploymentId);
        deployer.undeploy();

        TimeoutRepository.getTimeout("Jbpm.undeploy").waitFor(() -> !deployer.isDeploymentFinished());
    }

    @Override
    public void buildTest(final BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(process.getPackageID());
        builder.setLogDir(getJbpmInstallerPath());
        builder.setProcess(process);
        builder.buildTests();
    }

    protected JbpmApiBasedProcessInstanceOutcomeChecker createProcessOutcomeChecker(String name) {
        String url = getJbpmnUrl() + "/rest/runtime/" + getDeploymentId(name) + "/history/instance/1";
        String deployCheckUrl = getJbpmnUrl() + "/rest/deployment/" + getDeploymentId(name);
        return new JbpmApiBasedProcessInstanceOutcomeChecker(url, deployCheckUrl, getDeploymentId(name));
    }

}
