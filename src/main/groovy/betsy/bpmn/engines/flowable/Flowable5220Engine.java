package betsy.bpmn.engines.flowable;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNProcessStarter;
import betsy.bpmn.engines.BPMNTestcaseMerger;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.GenericBPMNTester;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.engines.activiti.ActivitiLogBasedProcessInstanceOutcomeChecker;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import pebl.ProcessLanguage;
import pebl.benchmark.test.TestCase;

import static betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS;

/**
 * Activiti 5.20.0
 *
 */
public class Flowable5220Engine extends AbstractBPMNEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPMN, "flowable", "5.22.0", LocalDate.of(2016, 10, 13), "Apache-2.0");
    }

    @Override
    public void install() {
        FlowableInstaller installer = new FlowableInstaller();
        installer.setFileName("flowable-5.22.0.zip");
        installer.setDestinationDir(getServerPath());
        installer.setGroovyFile(Optional.of("groovy-all-2.4.5.jar"));

        installer.install();

        // Modify preferences
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"),"<!--","");
        FileTasks.replaceTokenInFile(installer.getClassesPath().resolve("activiti-custom-context.xml"),"-->","");
    }

    private static final Logger LOGGER = Logger.getLogger(Flowable5220Engine.class);

    public static final String URL = "http://kermit:kermit@localhost:8080/flowable-rest";

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
                    new FlowableApiBasedProcessOutcomeChecker(),
                    new ActivitiLogBasedProcessInstanceOutcomeChecker(getTomcat().getTomcatLogsDir().resolve("activiti.log")),
                    new FlowableProcessStarter()
            ).runTest();
        }
        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    private Path getInstanceLogFile(String processName, int testCaseNumber) {
        return getTomcat().getTomcatBinDir().resolve("log-"+processName + "-" + testCaseNumber + ".txt");
    }

    @Override
    public BPMNProcessStarter getProcessStarter() {
        return new FlowableProcessStarter();
    }

    @Override public Path getLogForInstance(String processName, String instanceId) {
        return getInstanceLogFile(processName, Integer.parseInt(instanceId));
    }

    @Override
    public void deploy(String name, Path path) {
        deployBpmnProcess(path);
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
    public boolean isDeployed(QName process) {
        try {
            return !UNDEPLOYED_PROCESS.equals(new FlowableApiBasedProcessOutcomeChecker().checkProcessOutcome(process.getLocalPart()));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void undeploy(QName process) {
        LOGGER.info("Undeploying process " + process);
        try {
            JSONObject result = JsonHelper.get(URL + "/service/repository/deployments?name="+process.getLocalPart() +".bpmn", 200);
            String id = result.optJSONArray("data").optJSONObject(0).optString("id");
            JsonHelper.delete(URL + "/service/repository/deployments/" + id, 204);
        } catch (Exception e) {
            LOGGER.info("undeployment failed", e);
        }
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/activiti");
    }

    @Override
    public Path buildArchives(BPMNProcess process) {
        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"),
                process.getProcess(),
                process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"),
                "processName", process.getName());

        XSLTTasks.transform(getXsltPath().resolve("activiti.xsl"),
                process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"),
                process.getTargetProcessFilePath());

        FileTasks.deleteFile(process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"));

        return process.getTargetProcessFilePath();
    }

    @Override
    public void buildTest(BPMNProcess process) {
        BPMNTestBuilder builder = new BPMNTestBuilder();
        builder.setPackageString(process.getPackageID());
        builder.setLogDir(getTomcat().getTomcatBinDir());
        builder.setProcess(process);

        builder.buildTests();
    }

    @Override
    public String getEndpointUrl(String name) {
        return URL + "/service/repository/";
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

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getTomcat().getTomcatLogsDir()));
        result.addAll(FileTasks.findAllInFolder(getTomcat().getTomcatBinDir(), "log*.txt"));

        return result;
    }
}

