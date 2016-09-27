package betsy.bpmn.engines.camunda;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.engines.TestCaseUtil;
import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import pebl.benchmark.test.TestCase;
import pebl.benchmark.test.TestStep;
import pebl.benchmark.test.steps.DelayTestStep;
import pebl.benchmark.test.steps.GatherTracesTestStep;
import pebl.benchmark.test.steps.vars.Variable;
import pebl.benchmark.test.steps.vars.ProcessStartWithVariablesTestStep;

public class CamundaTester {

    private static final Logger LOGGER = Logger.getLogger(CamundaTester.class);

    private final TestCase testCase;
    private final Path logDir;
    private final Path instanceLogFile;
    private final BPMNTester bpmnTester;

    public CamundaTester(TestCase testCase, Path logDir, Path instanceLogFile, BPMNTester bpmnTester) {
        this.testCase = Objects.requireNonNull(testCase);
        this.logDir = Objects.requireNonNull(logDir);
        this.instanceLogFile = Objects.requireNonNull(instanceLogFile);
        this.bpmnTester = Objects.requireNonNull(bpmnTester);
    }

    /**
     * runs a single test
     */
    public void runTest() {
        String key = TestCaseUtil.getKey(testCase);

        Path logFile = FileTasks.findFirstMatchInFolder(logDir, "catalina*");

        BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeBeforeTest =
                new CamundaLogBasedProcessInstanceOutcomeChecker(logFile)
                        .checkProcessOutcome(key);
        if (outcomeBeforeTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
            BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_DEPLOYMENT);
        }

        for (TestStep testStep : testCase.getTestSteps()) {
            if (testStep instanceof ProcessStartWithVariablesTestStep) {
                try {
                    ProcessStartWithVariablesTestStep processStartWithVariablesTestStep = (ProcessStartWithVariablesTestStep) testStep;
                    List<Variable> variables = processStartWithVariablesTestStep.getVariables();
                    new CamundaProcessStarter().start(processStartWithVariablesTestStep.getProcess(), variables);
                } catch (Exception e) {
                    LOGGER.info("Could not start process", e);
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_RUNTIME);
                }
            } else if (testStep instanceof DelayTestStep) {
                WaitTasks.sleep(((DelayTestStep) testStep).getTimeToWaitAfterwards());
            } else if (testStep instanceof GatherTracesTestStep) {
                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest =
                        new CamundaLogBasedProcessInstanceOutcomeChecker(logFile)
                                .checkProcessOutcome(key);
                if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.RUNTIME) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_RUNTIME);
                } else if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
                }

                // Check on parallel execution
                BPMNEnginesUtil.checkParallelExecution(testCase, instanceLogFile);

                // Check whether MARKER file exists
                BPMNEnginesUtil.checkMarkerFileExists(testCase, instanceLogFile);

                // Check data type
                BPMNEnginesUtil.checkDataLog(testCase, instanceLogFile);

                BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, instanceLogFile);

                LOGGER.info("contents of log file " + instanceLogFile + ": " + FileTasks.readAllLines(instanceLogFile));

                bpmnTester.test();
            }
        }
    }

}
