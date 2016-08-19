package betsy.bpmn.engines.activiti;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;

import java.nio.file.Path;

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

        BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeBeforeTest = new ActivitiLogBasedProcessInstanceOutcomeChecker(logFile).checkProcessOutcome(key);
        if(outcomeBeforeTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_DEPLOYMENT);
        }

        try {
            if(testCase.hasParallelProcess()) {
                new ActivitiProcessStarter().start(BPMNTestCase.PARALLEL_PROCESS_KEY);
            }

            new ActivitiProcessStarter().start(key, testCase.getVariables());

            // Wait and check for errors only if process instantiation was successful
            WaitTasks.sleep(testCase.getDelay().orElse(0));

            BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest = new ActivitiLogBasedProcessInstanceOutcomeChecker(logFile).checkProcessOutcome(key);
            if(outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.RUNTIME) {
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            } else if(outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
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

            BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest = new ActivitiLogBasedProcessInstanceOutcomeChecker(logFile).checkProcessOutcome(key);
            if(outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.RUNTIME) {
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            } else if(outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
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
