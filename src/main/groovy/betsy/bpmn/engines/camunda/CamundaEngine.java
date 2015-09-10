package betsy.bpmn.engines.camunda;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.reporting.BPMNTestcaseMerger;
import betsy.common.config.Configuration;
import betsy.common.tasks.*;
import betsy.common.util.FileTypes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CamundaEngine extends AbstractBPMNEngine {

    @Override
    public String getName() {
        return "camunda";
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
    public void deploy(final BPMNProcess process) {
        FileTasks.copyFileIntoFolder(process.getTargetPath().resolve("war").resolve(process.getName() + ".war"), getTomcatDir().resolve("webapps"));

        //wait until it is deployed
        final Path logFile = FileTasks.findFirstMatchInFolder(getTomcatLogsDir(), "catalina*");
        if (logFile == null) {
            throw new IllegalStateException("Could not find catalina log file in " + getTomcatLogsDir());
        }

        WaitTasks.waitFor(20000, 500, () ->
                FileTasks.hasFileSpecificSubstring(logFile, "Process Application " + process.getName() + " Application successfully deployed.") ||
                        FileTasks.hasFileSpecificSubstring(logFile, "Context [/" + process.getName() + "] startup failed due to previous errors"));
    }

    @Override
    public void buildArchives(final BPMNProcess process) {
        Path targetWarWebinfClassesPath = process.getTargetPath().resolve("war/WEB-INF/classes");
        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"),
                process.getProcess(),
                targetWarWebinfClassesPath.resolve(process.getName() + ".bpmn-temp"));

        XSLTTasks.transform(getXsltPath().resolve("camunda.xsl"),
                targetWarWebinfClassesPath.resolve(process.getName() + ".bpmn-temp"),
                targetWarWebinfClassesPath.resolve(process.getName() + FileTypes.BPMN));

        FileTasks.deleteFile(targetWarWebinfClassesPath.resolve(process.getName() + ".bpmn-temp"));

        CamundaResourcesGenerator generator = new CamundaResourcesGenerator();
        generator.setGroupId(process.getGroupId());
        generator.setProcessName(process.getName());
        generator.setDestDir(process.getTargetPath().resolve("war"));
        generator.setVersion(process.getVersion());
        generator.generateWar();
    }

    @Override
    public void buildTest(final BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(getName() + "." + process.getGroup());
        builder.setLogDir(getTomcatDir().resolve("bin"));
        builder.setProcess(process);
        builder.buildTests();
    }

    @Override
    public String getEndpointUrl(BPMNProcess process) {
        return "http://localhost:8080/engine-rest/engine/default";
    }


    @Override
    public void storeLogs(BPMNProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());

        for(Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
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

        Map<String, String> map1 = new LinkedHashMap<>(2);
        map1.put("JAVA_HOME", pathToJava7.toString());
        map1.put("JRE_HOME", pathToJava7.toString());
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("camunda_startup.sh")), map1);

        WaitTasks.waitForAvailabilityOfUrl(30_000, 500, getCamundaUrl());
    }

    @Override
    public void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq Tomcat"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getServerPath().resolve("camunda_shutdown.sh")));
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(getCamundaUrl());
    }

    @Override
    public void testProcess(BPMNProcess process) {
        for (BPMNTestCase testCase : process.getTestCases()) {
            BPMNTester bpmnTester = new BPMNTester();
            bpmnTester.setSource(process.getTargetTestSrcPathWithCase(testCase.getNumber()));
            bpmnTester.setTarget(process.getTargetTestBinPathWithCase(testCase.getNumber()));
            bpmnTester.setReportPath(process.getTargetReportsPathWithCase(testCase.getNumber()));

            CamundaTester tester = new CamundaTester();
            tester.setTestCase(testCase);
            tester.setRestURL(getEndpointUrl(process));
            tester.setBpmnTester(bpmnTester);
            tester.setKey(process.getName());
            tester.setLogDir(getTomcatLogsDir());
            tester.runTest();
        }

        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

}
