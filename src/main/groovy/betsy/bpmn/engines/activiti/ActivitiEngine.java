package betsy.bpmn.engines.activiti;

import betsy.bpel.engines.tomcat.Tomcat;
import betsy.bpel.engines.tomcat.TomcatInstaller;
import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.engines.camunda.JsonHelper;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestBuilder;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.reporting.BPMNTestcaseMerger;
import betsy.common.config.Configuration;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.tasks.ZipTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Path;

public class ActivitiEngine extends BPMNEngine {
    @Override
    public void testProcess(BPMNProcess process) {
        for (BPMNTestCase testCase : process.getTestCases()) {
            ActivitiTester tester = new ActivitiTester();
            tester.setTestCase(testCase);
            tester.setTestSrc(process.getTargetTestSrcPathWithCase(testCase.getNumber()));
            tester.setTestBin(process.getTargetTestBinPathWithCase(testCase.getNumber()));
            tester.setRestURL(URL);
            tester.setReportPath(process.getTargetReportsPathWithCase(testCase.getNumber()));
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
        log.info("Deploying file " + bpmnFile.toAbsolutePath());
        try {
            JsonHelper.post(URL + "/service/repository/deployments", bpmnFile, 201);
        } catch (Exception e) {
            log.info("deployment failed", e);
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

        // TODO only copy log files from tomcat, the other files are files for the test
        FileTasks.copyFilesInFolderIntoOtherFolder(getTomcat().getTomcatLogsDir(), process.getTargetLogsPath());

        for (BPMNTestCase tc : process.getTestCases()) {
            Path tomcatLog = getTomcat().getTomcatBinDir().resolve("log" + tc.getNumber() + ".txt");
            FileTasks.copyFileIntoFolder(tomcatLog, process.getTargetLogsPath());
        }
    }

    @Override
    public void install() {
        // install tomcat
        TomcatInstaller tomcatInstaller = new TomcatInstaller();
        tomcatInstaller.setDestinationDir(getServerPath());
        tomcatInstaller.install();

        // unzip activiti
        String filename = "activiti-5.16.3.zip";
        NetworkTasks.downloadFile("https://github.com/Activiti/Activiti/releases/download/activiti-5.16.3/" + filename, Configuration.getDownloadsDir());
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(filename), getServerPath());

        // deploy
        getTomcat().deployWar(getServerPath().resolve("activiti-5.16.3").resolve("wars").resolve("activiti-rest.war"));

        String groovyFile = "groovy-all-2.1.3.jar";
        NetworkTasks.downloadFile("http://central.maven.org/maven2/org/codehaus/groovy/groovy-all/2.1.3/" + groovyFile, Configuration.getDownloadsDir());
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
        FileTasks.replaceTokenInFile(classes.resolve("activiti-context.xml"),"\t\t<property name=\"jobExecutorActivate\" value=\"false\" />","\t\t<property name=\"jobExecutorActivate\" value=\"true\" />");
    }

    public Tomcat getTomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.setEngineDir(getServerPath());
        return tomcat;
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

    private static final Logger log = Logger.getLogger(ActivitiEngine.class);
    public static final String URL = "http://kermit:kermit@localhost:8080/activiti-rest";
}
