package betsy.bpmn.engines.activiti;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.reporting.BPMNTestcaseMerger;
import betsy.common.model.ProcessLanguage;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ActivitiEngine extends AbstractBPMNEngine {
    @Override
    public void testProcess(BPMNProcess process) {
        for (BPMNTestCase testCase : process.getTestCases()) {
            BPMNTester bpmnTester = new BPMNTester();
            bpmnTester.setSource(process.getTargetTestSrcPathWithCase(testCase.getNumber()));
            bpmnTester.setTarget(process.getTargetTestBinPathWithCase(testCase.getNumber()));
            bpmnTester.setReportPath(process.getTargetReportsPathWithCase(testCase.getNumber()));

            ActivitiTester tester = new ActivitiTester();
            tester.setTestCase(testCase);
            tester.setBpmnTester(bpmnTester);
            tester.setKey(process.getName());
            tester.setLogDir(getTomcat().getTomcatLogsDir());

            tester.runTest();
        }
        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPMN, "activiti", "5.16.3");
    }

    @Override
    public void deploy(BPMNProcess process) {
        deployBpmnProcess(process.getTargetProcessFilePath());
    }

    public static void deployBpmnProcess(Path bpmnFile) {
        LOGGER.info("Deploying file " + bpmnFile.toAbsolutePath());
        try {
            JsonHelper.post(URL + "/service/repository/deployments", bpmnFile, 201);
        } catch (Exception e) {
            LOGGER.info("deployment failed", e);
        }
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/activiti");
    }

    @Override
    public void buildArchives(BPMNProcess process) {
        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"),
                process.getProcess(),
                process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"));

        XSLTTasks.transform(getXsltPath().resolve("activiti.xsl"),
                process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"),
                process.getTargetProcessFilePath());

        FileTasks.deleteFile(process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"));
    }

    @Override
    public void buildTest(BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(process.getEngineID() + "." + process.getGroup().getName() + "." + process.getName());
        builder.setLogDir(getTomcat().getTomcatBinDir());
        builder.setProcess(process);

        builder.buildTests();
    }

    @Override
    public String getEndpointUrl(BPMNProcess process) {
        return URL + "/service/repository/";
    }

    @Override
    public void storeLogs(BPMNProcess process) {
        Path targetLogsPath = process.getTargetLogsPath();
        FileTasks.mkdirs(targetLogsPath);

        for(Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
    }

    @Override
    public void install() {
        ActivitiInstaller installer = new ActivitiInstaller();
        installer.setFileName("activiti-5.16.3.zip");
        installer.setDestinationDir(getServerPath());
        installer.setGroovyFile(Optional.of("groovy-all-2.1.3.jar"));

        installer.install();

        // Modify preferences
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
    }

    public Tomcat getTomcat() {
        return Tomcat.v7(getServerPath());
    }

    @Override
    public void startup() {
        getTomcat().startup();
    }

    @Override
    public void shutdown() {
        getTomcat().shutdown();
    }

    @Override
    public boolean isRunning() {
        return getTomcat().checkIfIsRunning();
    }

    private static final Logger LOGGER = Logger.getLogger(ActivitiEngine.class);

    public static final String URL = "http://kermit:kermit@localhost:8080/activiti-rest";

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getTomcat().getTomcatLogsDir()));
        result.addAll(FileTasks.findAllInFolder(getTomcat().getTomcatBinDir(), "log*.txt"));

        return result;
    }
}
