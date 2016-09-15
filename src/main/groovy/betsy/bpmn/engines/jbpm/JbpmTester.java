package betsy.bpmn.engines.jbpm;

import java.nio.file.Path;
import java.util.List;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.TestCaseUtil;
import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import pebl.test.TestCase;
import pebl.test.TestStep;
import pebl.test.steps.DelayTestStep;
import pebl.test.steps.GatherAndAssertTracesTestStep;
import pebl.test.steps.vars.Variable;
import pebl.test.steps.vars.ProcessStartWithVariablesTestStep;

public class JbpmTester {

    private final TestCase testCase;
    private final BPMNTester bpmnTester;
    private final JbpmApiBasedProcessInstanceOutcomeChecker processInstanceOutcomeChecker;
    private final Path logDir;
    private final Path serverLogFile;

    private static final Logger LOGGER = Logger.getLogger(JbpmTester.class);

    public JbpmTester(TestCase testCase, BPMNTester bpmnTester,
                      JbpmApiBasedProcessInstanceOutcomeChecker processInstanceOutcomeChecker,
                      Path logDir, Path serverLogFile) {
        this.testCase = testCase;
        this.bpmnTester = bpmnTester;
        this.processInstanceOutcomeChecker = processInstanceOutcomeChecker;
        this.logDir = logDir;
        this.serverLogFile = serverLogFile;
    }

    /**
     * Runs a single test
     */
    public void runTest() {
        String key = TestCaseUtil.getKey(testCase);

        BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeBeforeTest =
                new JbpmLogBasedProcessInstanceOutcomeChecker(serverLogFile).checkProcessOutcome(key);
        if (outcomeBeforeTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
            BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_DEPLOYMENT);
        }

        for (TestStep testStep : testCase.getTestSteps()) {
            if (testStep instanceof ProcessStartWithVariablesTestStep) {
                try {
                    ProcessStartWithVariablesTestStep processStartWithVariablesTestStep = (ProcessStartWithVariablesTestStep) testStep;
                    List<Variable> variables = processStartWithVariablesTestStep.getVariables();
                    new JbpmProcessStarter().start(processStartWithVariablesTestStep.getProcess(), variables);
                } catch (Exception e) {
                    LOGGER.info("Could not start process", e);
                    BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_RUNTIME);
                }
            } else if (testStep instanceof DelayTestStep) {
                WaitTasks.sleep(((DelayTestStep) testStep).getTimeToWaitAfterwards());
            } else if (testStep instanceof GatherAndAssertTracesTestStep) {
                // Check on parallel execution
                BPMNEnginesUtil.checkParallelExecution(testCase, logDir);

                // Check whether MARKER file exists
                BPMNEnginesUtil.checkMarkerFileExists(testCase, logDir);

                // Check data type
                BPMNEnginesUtil.checkDataLog(testCase, logDir);

                BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, logDir);

                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcome = processInstanceOutcomeChecker.checkProcessOutcome(key);
                if (outcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED) {
                    BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_PROCESS_ABORTED);
                } else if (outcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                    BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
                } else if (outcome == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN) {
                    BPMNAssertions.appendToFile(logDir, BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
                }

                LOGGER.info("contents of log file " + logDir + ": " + FileTasks.readAllLines(logDir));

                bpmnTester.test();
            }
        }
    }

}
