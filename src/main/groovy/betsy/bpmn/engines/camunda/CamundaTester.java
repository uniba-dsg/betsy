package betsy.bpmn.engines.camunda;

import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.LogFileAnalyzer;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestCaseVariable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CamundaTester {
    /**
     * runs a single test
     */
    public void runTest() {
        //make bin dir
        FileTasks.mkdirs(testBin);
        FileTasks.mkdirs(reportPath);

        Path logFile = FileTasks.findFirstMatchInFolder(logDir, "catalina*");

        addDeploymentErrorsToLogFile(logFile);

        if (!testCase.getSelfStarting()) {
            //first request to get id
            JSONObject response = JsonHelper.get(restURL + "/process-definition?key=" + key, 200);
            final String id = String.valueOf(response.get("id"));

            //assembling JSONObject for second request
            JSONObject requestBody = new JSONObject();
            requestBody.put("variables", mapToArrayWithMaps(testCase.getVariables()));
            requestBody.put("businessKey", "key-" + key);

            //second request to start process using id and Json to get the process instance id
            try {
                JsonHelper.post(restURL + "/process-definition/" + id + "/start?key=" + key, requestBody, 200);
            } catch (Exception e){
                log.info("Starting failed", e);
            }
        }

        WaitTasks.sleep(testCase.getDelay());
        addRuntimeErrorsToLogFile(logFile);

        BPMNTester.setupPathToToolsJarForJavacAntTask(this);
        BPMNTester.compileTest(testSrc, testBin);
        BPMNTester.executeTest(testSrc, testBin, reportPath);
    }

    private void addDeploymentErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer = new LogFileAnalyzer(logFile);
        analyzer.addSubstring("org.camunda.bpm.engine.ProcessEngineException", "runtimeException");
        analyzer.addSubstring("EndEvent_2 throws error event with errorCode 'ERR-1'", "thrownErrorEvent");
        for (String deploymentError : analyzer.getErrors()) {
            BPMNTester.appendToFile(getFileName(), deploymentError);
        }
    }

    private void addRuntimeErrorsToLogFile(Path logFile) {
        LogFileAnalyzer analyzer2 = new LogFileAnalyzer(logFile);
        analyzer2.addSubstring("Ignoring unsupported activity type");
        analyzer2.addSubstring("org.camunda.bpm.engine.ProcessEngineException");
        for (String deploymentError : analyzer2.getErrors()) {
            BPMNTester.appendToFile(getFileName(), deploymentError);
        }
    }

    public static Map<String, Object> mapToArrayWithMaps(List<BPMNTestCaseVariable> variables) {
        Map<String, Object> map = new HashMap<>();

        for (BPMNTestCaseVariable entry : variables) {
            Map<String, Object> submap = new HashMap<>();
            submap.put("value", entry.getValue());
            submap.put("type", entry.getType());
            map.put(entry.getName(), submap);
        }


        return map;
    }

    private Path getFileName() {
        return logDir.resolve("..").normalize().resolve("bin").resolve("log" + testCase.getNumber() + ".txt");
    }

    public BPMNTestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(BPMNTestCase testCase) {
        this.testCase = testCase;
    }

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Path getLogDir() {
        return logDir;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    private BPMNTestCase testCase;
    private String restURL;
    private Path reportPath;
    private Path testBin;
    private Path testSrc;
    private String key;
    private Path logDir;

    private static final Logger log = Logger.getLogger(CamundaTester.class);
}
