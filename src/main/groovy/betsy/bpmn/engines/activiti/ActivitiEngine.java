package betsy.bpmn.engines.activiti;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.camunda.JsonHelper;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.reporting.BPMNTestcaseMerger;
import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.tasks.ZipTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

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
            tester.setRestURL(URL);
            tester.setBpmnTester(bpmnTester);
            tester.setKey(process.getName());
            tester.setLogDir(getTomcat().getTomcatLogsDir());

            tester.runTest();
        }
        new BPMNTestcaseMerger(process.getTargetReportsPath()).mergeTestCases();
    }

    @Override
    public String getName() {
        return "activiti";
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
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/camunda");
    }

    @Override
    public void buildArchives(BPMNProcess process) {
        XSLTTasks.transform(getXsltPath().resolve("../scriptTask.xsl"),
                process.getProcess(),
                process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"));

        XSLTTasks.transform(getXsltPath().resolve("camunda.xsl"),
                process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"),
                process.getTargetProcessFilePath());

        FileTasks.deleteFile(process.getTargetProcessPath().resolve(process.getName() + ".bpmn-temp"));
    }

    @Override
    public void buildTest(BPMNProcess process) {
        BPMNTestBuilder bpmnTestBuilder = new BPMNTestBuilder();
        bpmnTestBuilder.setPackageString(getName() + "." + process.getGroup());
        bpmnTestBuilder.setLogDir(getTomcat().getTomcatBinDir());
        bpmnTestBuilder.setProcess(process);

        bpmnTestBuilder.buildTests();
    }

    @Override
    public String getEndpointUrl(BPMNProcess process) {
        return URL + "/service/repository/";
    }

    @Override
    public void storeLogs(BPMNProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());

        for(Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
    }

    @Override
    public void install() {
        // install tomcat
        TomcatInstaller.v7(getServerPath()).install();

        // unzip activiti
        String filename = "activiti-5.16.3.zip";
        NetworkTasks.downloadFileFromBetsyRepo(filename);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(filename), getServerPath());

        // deploy
        getTomcat().deployWar(getServerPath().resolve("activiti-5.16.3").resolve("wars").resolve("activiti-rest.war"));

        String groovyFile = "groovy-all-2.1.3.jar";
        NetworkTasks.downloadFileFromBetsyRepo(groovyFile);
        getTomcat().addLib(Configuration.getDownloadsDir().resolve(groovyFile));

        Path classes = getTomcat().getTomcatWebappsDir().resolve("activiti-rest").resolve("WEB-INF").resolve("classes");
        FileTasks.createFile(classes.resolve("log4j.properties"), "log4j.rootLogger=DEBUG, CA, FILE\n" +
                "\n" +
                "# ConsoleAppender\n" +
                "log4j.appender.CA=org.apache.log4j.ConsoleAppender\n" +
                "log4j.appender.CA.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.CA.layout.ConversionPattern= %d{hh:mm:ss,SSS} [%t] %-5p %c %x - %m%n\n" +
                "\n" +
                "log4j.appender.FILE=org.apache.log4j.DailyRollingFileAppender\n" +
                "log4j.appender.FILE.File=${catalina.base}/logs/activiti.log\n" +
                "log4j.appender.FILE.Append=true\n" +
                "log4j.appender.FILE.Encoding=UTF-8\n" +
                "log4j.appender.FILE.layout=org.apache.log4j.PatternLayout\n" +
                "log4j.appender.FILE.layout.ConversionPattern=%d{ABSOLUTE} %-5p [%c{1}] %m%n\n");
        FileTasks.replaceTokenInFile(classes.resolve("activiti-context.xml"), "\t\t<property name=\"jobExecutorActivate\" value=\"false\" />", "\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
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
