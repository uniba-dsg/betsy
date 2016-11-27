package betsy.bpmn.engines.camunda;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.BPMNTestcaseMerger;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.GenericBPMNTester;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.common.config.Configuration;
import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.URLTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.FileTypes;
import org.apache.log4j.Logger;
import pebl.ProcessLanguage;
import pebl.benchmark.test.TestCase;

import static betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS;

public class CamundaEngine extends AbstractBPMNEngine {

    private static final Logger LOGGER = Logger.getLogger(CamundaEngine.class);


    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "camunda", "7.0.0", LocalDate.of(2013, 8, 31), "Apache-2.0");
    }

    public String getCamundaUrl() {
        return "http://localhost:8080";
    }

    public String getTomcatName() {
        return "apache-tomcat-7.0.33";
    }

    public Path getTomcatDir() {
        return getServerPath().resolve("server").resolve(getTomcatName());
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/camunda");
    }

    @Override
    public void deploy(String name, Path path) {
        FileTasks.copyFileIntoFolder(path, getTomcatDir().resolve("webapps"));

        //wait until it is deployed
        final Path logFile = FileTasks.findFirstMatchInFolder(getTomcatLogsDir(), "catalina*");
        if (logFile == null) {
            throw new IllegalStateException("Could not find catalina log file in " + getTomcatLogsDir());
        }

        TimeoutRepository.getTimeout("Camunda.deploy").waitFor(() ->
                FileTasks.hasFileSpecificSubstring(logFile, "Process Application " + name + " Application successfully deployed.") ||
                        FileTasks.hasFileSpecificSubstring(logFile, "Process application " + name + " Application successfully deployed") ||
                        FileTasks.hasFileSpecificSubstring(logFile, "Context [/" + name + "] startup failed due to previous errors"));
    }

    @Override
    public boolean isDeployed(QName process) {
        try {
            return !UNDEPLOYED_PROCESS.equals(new CamundaApiBasedProcessInstanceOutcomeChecker().checkProcessOutcome(process.getLocalPart()));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void undeploy(QName process) {
        LOGGER.info("Undeploying process " + process);
        FileTasks.deleteFile(getTomcatDir().resolve("webapps").resolve(process.getLocalPart() + ".war"));
        WaitTasks.sleep(5000);
    }

    @Override
    public Path buildArchives(final BPMNProcess process) {
        Path targetProcessPath = process.getTargetProcessPath();
        FileTasks.mkdirs(targetProcessPath);
        FileTasks.copyFileIntoFolder(process.getProcess(), targetProcessPath);

        Path targetProcessFilePath = targetProcessPath.resolve(process.getProcessFileName());
        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"),
                targetProcessFilePath,
                targetProcessPath.resolve(process.getName() + ".bpmn-temp"),
                "processName", process.getName());
        XSLTTasks.transform(getXsltPath().resolve("camunda.xsl"),
                targetProcessPath.resolve(process.getName() + ".bpmn-temp"),
                targetProcessPath.resolve(process.getName() + FileTypes.BPMN));

        FileTasks.deleteFile(targetProcessPath.resolve(process.getName() + ".bpmn-temp"));


        Path targetWarWebinfClassesPath = process.getTargetPath().resolve("war/WEB-INF/classes");
        FileTasks.mkdirs(targetWarWebinfClassesPath);
        FileTasks.copyFileIntoFolder(targetProcessFilePath, targetWarWebinfClassesPath);

        CamundaResourcesGenerator generator = new CamundaResourcesGenerator();
        generator.setGroupId("de.uniba.dsg");
        generator.setProcessName(process.getName());
        Path warFile = process.getTargetPath().resolve("war");
        generator.setDestDir(warFile);
        generator.setVersion("1.0");
        return generator.generateWar();
    }

    @Override
    public void buildTest(final BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(process.getPackageID());
        builder.setLogDir(getTomcatDir().resolve("bin"));
        builder.setProcess(process);
        builder.buildTests();
    }

    @Override
    public String getEndpointUrl(String name) {
        return "http://localhost:8080/engine-rest/engine/default";
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getTomcatLogsDir()));
        result.addAll(FileTasks.findAllInFolder(getTomcatDir().resolve("bin"), "log*.txt"));

        return result;
    }

    private Path getTomcatLogsDir() {
        return getTomcatDir().resolve("logs");
    }

    @Override
    public void install() {
        CamundaInstaller installer = new CamundaInstaller();
        installer.setDestinationDir(getServerPath());
        installer.setTomcatName(getTomcatName());
        installer.setGroovyFile(Optional.of("groovy-all-2.2.0.jar"));
        installer.install();
    }

    @Override
    public void startup() {
        Path pathToJava7 = Configuration.getJava7Home();
        Map<String, String> map = new LinkedHashMap<>(2);
        map.put("JAVA_HOME", pathToJava7.toString());
        map.put("JRE_HOME", pathToJava7.toString());

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath(), "camunda_startup.bat"), map);
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("camunda_startup.sh")), map);

        TimeoutRepository.getTimeout("Camunda.startup").waitForAvailabilityOfUrl(getCamundaUrl());
    }

    @Override
    public void shutdown() {
        if (!Files.exists(getServerPath())) {
            LOGGER.info("Shutdown of " + getName() + " not possible as " + getServerPath() + " does not exist.");
            return;
        }

        Path shutdownShellScript = getServerPath().resolve("camunda_shutdown.sh");
        if (!Files.exists(shutdownShellScript)) {
            LOGGER.info("Shutdown shell script " + shutdownShellScript + " does not exist");
        }

        Path pathToJava7 = Configuration.getJava7Home();
        Map<String, String> map = new LinkedHashMap<>(2);
        map.put("JAVA_HOME", pathToJava7.toString());
        map.put("JRE_HOME", pathToJava7.toString());

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("camunda_shutdown.bat")), map);
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq Tomcat"));

        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(shutdownShellScript), map);
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(getCamundaUrl());
    }

    @Override
    public void testProcess(BPMNProcess process) {
        for (TestCase testCase : process.getTestCases()) {
            BPMNTester bpmnTester = new BPMNTester();
            int testCaseNumber = testCase.getNumber();
            bpmnTester.setSource(process.getTargetTestSrcPathWithCase(testCaseNumber));
            bpmnTester.setTarget(process.getTargetTestBinPathWithCase(testCaseNumber));
            bpmnTester.setReportPath(process.getTargetReportsPathWithCase(testCaseNumber));

            new GenericBPMNTester(process,
                    testCase,
                    getInstanceLogFile(process.getName(), testCaseNumber),
                    bpmnTester,
                    new CamundaApiBasedProcessInstanceOutcomeChecker(),
                    new CamundaLogBasedProcessInstanceOutcomeChecker(FileTasks.findFirstMatchInFolder(getTomcatLogsDir(), "catalina*")),
                    new CamundaProcessStarter()
            ).runTest();
        }

        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    private Path getInstanceLogFile(String processName, int testCaseNumber) {
        return getTomcatDir().resolve("bin").resolve("log-" + processName + "-" + testCaseNumber + ".txt");
    }

    @Override
    public BPMNProcessStarter getProcessStarter() {
        return new CamundaProcessStarter();
    }

    @Override
    public Path getLogForInstance(String processName, String instanceId) {
        return getInstanceLogFile(processName, Integer.parseInt(instanceId));
    }

}
