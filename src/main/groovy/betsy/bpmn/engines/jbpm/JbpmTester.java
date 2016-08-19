package betsy.bpmn.engines.jbpm;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNProcessOutcomeChecker;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.LogFileAnalyzer;
import betsy.bpmn.engines.JsonHelper;

import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.Variable;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.nio.file.Path;

public class JbpmTester {

    private BPMNTestCase testCase;

    private String name;

    private String deploymentId;

    private BPMNTester bpmnTester;

    private Path logDir;

    private Path serverLogFile;

    private BPMNProcessOutcomeChecker processOutcomeChecker;

    private static final Logger LOGGER = Logger.getLogger(JbpmTester.class);

    /**
     * Runs a single test
     */
    public void runTest() {
        BPMNProcessOutcomeChecker.ProcessOutcome outcomeBeforeTest = new JbpmLogBasedProcessOutcomeChecker(serverLogFile).checkProcessOutcome(name);
        if(outcomeBeforeTest == BPMNProcessOutcomeChecker.ProcessOutcome.UNDEPLOYED) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_DEPLOYMENT);
        }

        if (testCase.hasParallelProcess()) {
            try {
                new JbpmProcessStarter(getDeploymentId()).start(BPMNTestCase.PARALLEL_PROCESS_KEY);
            } catch (RuntimeException e) {
                BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
            }
        }

        try {
            new JbpmProcessStarter(getDeploymentId()).start(name, testCase.getVariables());
        } catch (RuntimeException e) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_RUNTIME);
        }

        //delay for timer intermediate event
        WaitTasks.sleep(testCase.getDelay().orElse(0));

        // Check on parallel execution
        BPMNEnginesUtil.checkParallelExecution(testCase, getFileName());

        // Check whether MARKER file exists
        BPMNEnginesUtil.checkMarkerFileExists(testCase, getFileName());

        // Check data type
        BPMNEnginesUtil.checkDataLog(testCase, getFileName());

        BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, getFileName());

        BPMNProcessOutcomeChecker.ProcessOutcome outcome = new JbpmProcessOutcomeChecker(getDeploymentId())
                .checkProcessOutcome(getName());
        if(outcome == BPMNProcessOutcomeChecker.ProcessOutcome.PROCESS_ABORTED) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_PROCESS_ABORTED);
        } else if(outcome == BPMNProcessOutcomeChecker.ProcessOutcome.PROCESS_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
        } else if(outcome == BPMNProcessOutcomeChecker.ProcessOutcome.PROCESS_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN) {
            BPMNAssertions.appendToFile(getFileName(), BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
        }

        LOGGER.info("contents of log file " + getFileName() + ": " + FileTasks.readAllLines(getFileName()));

        bpmnTester.test();
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

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public void setServerLogFile(Path serverLogFile) {
        this.serverLogFile = serverLogFile;
    }

    public void setBpmnTester(BPMNTester bpmnTester) {
        this.bpmnTester = bpmnTester;
    }

    public void setProcessOutcomeChecker(BPMNProcessOutcomeChecker processOutcomeChecker) {
        this.processOutcomeChecker = processOutcomeChecker;
    }
}
