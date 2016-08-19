package betsy.bpmn.engines.activiti;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNProcessOutcomeChecker;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.JsonHelper;
import betsy.bpmn.engines.camunda.CamundaLogBasedProcessOutcomeChecker;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.Variable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ActivitiTester {
    private static final Logger LOGGER = Logger.getLogger(ActivitiTester.class);
    private BPMNTestCase testCase;
    private String key;
    private Path logDir;
    private BPMNTester bpmnTester;

    /**
     * runs a single test
     */
    public void runTest() {
        Path logFile = logDir.resolve("activiti.log");

        BPMNProcessOutcomeChecker.ProcessOutcome outcomeBeforeTest = new ActivitiLogBasedProcessOutcomeChecker(logFile).checkProcessOutcome(key);
        if(outcomeBeforeTest == BPMNProcessOutcomeChecker.ProcessOutcome.UNDEPLOYED) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_DEPLOYMENT);
        }

        try {
            if(testCase.hasParallelProcess()) {
                new ActivitiProcessStarter().start(BPMNTestCase.PARALLEL_PROCESS_KEY);
            }

            new ActivitiProcessStarter().start(key, testCase.getVariables());

            // Wait and check for errors only if process instantiation was successful
            WaitTasks.sleep(testCase.getDelay().orElse(0));

            BPMNProcessOutcomeChecker.ProcessOutcome outcomeAfterTest = new ActivitiLogBasedProcessOutcomeChecker(logFile).checkProcessOutcome(key);
            if(outcomeAfterTest == BPMNProcessOutcomeChecker.ProcessOutcome.RUNTIME) {
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            } else if(outcomeAfterTest == BPMNProcessOutcomeChecker.ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
            }

            // Check on parallel execution
            BPMNEnginesUtil.checkParallelExecution(testCase, getFileName());

            // Check whether MARKER file exists
            BPMNEnginesUtil.checkMarkerFileExists(testCase, getFileName());

            // Check data type
            BPMNEnginesUtil.checkDataLog(testCase, getFileName());
        } catch (Exception e) {
            LOGGER.info("Could not start process", e);

            BPMNProcessOutcomeChecker.ProcessOutcome outcomeAfterTest = new ActivitiLogBasedProcessOutcomeChecker(logFile).checkProcessOutcome(key);
            if(outcomeAfterTest == BPMNProcessOutcomeChecker.ProcessOutcome.RUNTIME) {
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            } else if(outcomeAfterTest == BPMNProcessOutcomeChecker.ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
            }
        }

        BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, getFileName());

        LOGGER.info("contents of log file " + getFileName() + ": " + FileTasks.readAllLines(getFileName()));

        bpmnTester.test();
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public void setBpmnTester(BPMNTester bpmnTester) {
        this.bpmnTester = bpmnTester;
    }
}
