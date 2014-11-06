package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.LogFileAnalyzer;
import betsy.bpmn.engines.camunda.JsonHelper;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestCaseVariable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class JbpmTester {
    /**
     * Runs a single test
     */
    public void runTest() {
        //make bin dir
        FileTasks.mkdirs(testBin);
        FileTasks.mkdirs(reportPath);

        addDeploymentErrorsToLogFile(serverLogFile);

        //setup variables and start process
        Map<String, Object> variables = new HashMap<>();
        for (BPMNTestCaseVariable variable : testCase.getVariables()) {
            variables.put(variable.getName(), variable.getValue());
        }

        StringJoiner joiner = new StringJoiner("&", "?", "");
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            joiner.add("map_" + entry.getKey() + "=" + entry.getValue());
        }

        String requestUrl = processStartUrl + joiner.toString();
        try {
            log.info("Trying to start process \"" + name + "\".");
            JsonHelper.postStringWithAuth(requestUrl, new JSONObject(), 200, user, password);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("No runtime manager could be found")) {
                log.info("Instantiation failed as no runtime manager could be found. Retrying in 10000ms.");
                //retry after delay
                WaitTasks.sleep(10000);
                try {
                    JsonHelper.postStringWithAuth(requestUrl, new JSONObject(), 200, user, password);
                } catch (RuntimeException innerEx) {
                    log.info(BPMNAssertions.ERROR_RUNTIME + ": Instantiation still not possible. Aborting test.", innerEx);
                    BPMNTester.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
                }
            } else {
                log.info(BPMNAssertions.ERROR_RUNTIME + ": Instantiation of process failed. Reason:", ex);
                BPMNTester.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            }
        }

        //delay for timer intermediate event
        WaitTasks.sleep(testCase.getDelay());

        checkProcessOutcome();


        BPMNTester.setupPathToToolsJarForJavacAntTask(this);
        BPMNTester.compileTest(testSrc, testBin);
        BPMNTester.executeTest(testSrc, testBin, reportPath);
    }

    private void checkProcessOutcome() {
        try {
            log.info("Trying to check process result status for " + name);
            String result = JsonHelper.getStringWithAuth(processHistoryUrl, 200, user, password);
            if (result.contains("ERR-1")) {
                log.info("Process has been aborted. Error with id ERR-1 detected.");
                BPMNTester.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
            } else if (result.contains("ESC_1")) {
                log.info("Process has been aborted. Escalation with id ESC_1 detected.");
                BPMNTester.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
            } else if (result.contains("<status>3</status>")) {
                log.info("Process has been aborted with unknown error.");
                BPMNTester.appendToFile(getFileName(), BPMNAssertions.ERROR_PROCESS_ABORTED);
            } else if (result.contains("<status>1</status>")) {
                log.info("Process completed normally.");
            }

        } catch (RuntimeException innerEx) {
            log.info("Checking process result status failed.", innerEx);
        }
    }

    private void addDeploymentErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("failed to deploy", BPMNAssertions.ERROR_DEPLOYMENT);
        for (BPMNAssertions deploymentError : analyzer.getErrors()) {
            BPMNTester.appendToFile(getFileName(), deploymentError);
            log.info(BPMNAssertions.ERROR_DEPLOYMENT + ": " + deploymentId + ", " + name + ": Deployment error detected.");
        }
    }

    private Path getFileName() {
        return logDir.resolve("log" + testCase.getNumber() + ".txt");
    }

    public BPMNTestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(BPMNTestCase testCase) {
        this.testCase = testCase;
    }

    public String getProcessStartUrl() {
        return processStartUrl;
    }

    public void setProcessStartUrl(String processStartUrl) {
        this.processStartUrl = processStartUrl;
    }

    public String getProcessHistoryUrl() {
        return processHistoryUrl;
    }

    public void setProcessHistoryUrl(String processHistoryUrl) {
        this.processHistoryUrl = processHistoryUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public Path getReportPath() {
        return reportPath;
    }

    public void setReportPath(Path reportPath) {
        this.reportPath = reportPath;
    }

    public Path getTestBin() {
        return testBin;
    }

    public void setTestBin(Path testBin) {
        this.testBin = testBin;
    }

    public Path getTestSrc() {
        return testSrc;
    }

    public void setTestSrc(Path testSrc) {
        this.testSrc = testSrc;
    }

    public Path getLogDir() {
        return logDir;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public Path getServerLogFile() {
        return serverLogFile;
    }

    public void setServerLogFile(Path serverLogFile) {
        this.serverLogFile = serverLogFile;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private BPMNTestCase testCase;
    private String processStartUrl;
    private String processHistoryUrl;
    private String name;
    private String deploymentId;
    private Path reportPath;
    private Path testBin;
    private Path testSrc;
    private Path logDir;
    private Path serverLogFile;
    private String user = "admin";
    private String password = "admin";

    private static final Logger log = Logger.getLogger(JbpmTester.class);

}
