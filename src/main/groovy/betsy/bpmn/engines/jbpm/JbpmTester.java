package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;

public class JbpmTester {

    private BPMNTestCase testCase;

    private String name;

    private String deploymentId;

    private BPMNTester bpmnTester;

    private Path logDir;

    private Path serverLogFile;

    private BPMNProcessInstanceOutcomeChecker processOutcomeChecker;

    private static final Logger LOGGER = Logger.getLogger(JbpmTester.class);

    /**
     * Runs a single test
     */
    public void runTest() {
        BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeBeforeTest =
                new JbpmLogBasedProcessInstanceOutcomeChecker(serverLogFile).checkProcessOutcome(name);
        if(outcomeBeforeTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
            BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_DEPLOYMENT);
        }

        if (testCase.hasParallelProcess()) {
            try {
                new JbpmProcessStarter().start(BPMNTestCase.PARALLEL_PROCESS_KEY);
            } catch (RuntimeException e) {
                BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_RUNTIME);
            }
        }

        try {
            new JbpmProcessStarter().start(name, testCase.getVariables());
        } catch (RuntimeException e) {
            BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_RUNTIME);
        }

        //delay for timer intermediate event
        WaitTasks.sleep(testCase.getDelay().orElse(0));

        // Check on parallel execution
        BPMNEnginesUtil.checkParallelExecution(testCase, logDir);

        // Check whether MARKER file exists
        BPMNEnginesUtil.checkMarkerFileExists(testCase, logDir);

        // Check data type
        BPMNEnginesUtil.checkDataLog(testCase, logDir);

        BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, logDir);

        BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcome = new JbpmApiBasedProcessInstanceOutcomeChecker(getDeploymentId())
                .checkProcessOutcome(getName());
        if(outcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED) {
            BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_PROCESS_ABORTED);
        } else if(outcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
            BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
        } else if(outcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN) {
            BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
        }

        LOGGER.info("contents of log file " + logDir + ": " + FileTasks.readAllLines(logDir));

        bpmnTester.test();
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

    public void setProcessOutcomeChecker(BPMNProcessInstanceOutcomeChecker processOutcomeChecker) {
        this.processOutcomeChecker = processOutcomeChecker;
    }
}
