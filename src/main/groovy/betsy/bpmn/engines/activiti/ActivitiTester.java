package betsy.bpmn.engines.activiti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.engines.BPMNEnginesUtil;
import betsy.bpmn.engines.BPMNProcessInstanceOutcomeChecker;
import betsy.bpmn.engines.BPMNTester;
import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import pebl.test.TestCase;
import pebl.test.TestStep;
import pebl.test.steps.DelayTestStep;
import pebl.test.steps.DeployableCheckTestStep;
import pebl.test.steps.GatherTracesTestStep;
import pebl.test.steps.vars.ProcessStartWithVariablesTestStep;
import pebl.test.steps.vars.Variable;

public class ActivitiTester {
    private static final Logger LOGGER = Logger.getLogger(ActivitiTester.class);

    private final BPMNProcess bpmnProcess;
    private final TestCase testCase;
    private final Path logDir;
    private final Path instanceLogFile;
    private final BPMNTester bpmnTester;

    public ActivitiTester(BPMNProcess bpmnProcess, TestCase testCase, Path logDir, Path instanceLogFile, BPMNTester bpmnTester) {
        this.bpmnProcess = bpmnProcess;
        this.testCase = Objects.requireNonNull(testCase);
        this.logDir = Objects.requireNonNull(logDir);
        this.instanceLogFile = Objects.requireNonNull(instanceLogFile);
        this.bpmnTester = Objects.requireNonNull(bpmnTester);
    }

    /**
     * runs a single test
     */
    public void runTest() {

        Path logFile = logDir.resolve("activiti.log");

        for (TestStep testStep : testCase.getTestSteps()) {
            if (testStep instanceof DeployableCheckTestStep) {
                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeBeforeTest = new ActivitiApiBasedProcessOutcomeChecker()
                        .checkProcessOutcome(bpmnProcess.getName());
                if (outcomeBeforeTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_DEPLOYMENT);
                }
            } else if (testStep instanceof ProcessStartWithVariablesTestStep) {
                try {
                    ProcessStartWithVariablesTestStep processStartWithVariablesTestStep = (ProcessStartWithVariablesTestStep) testStep;
                    List<Variable> variables = processStartWithVariablesTestStep.getVariables();
                    new ActivitiProcessStarter().start(processStartWithVariablesTestStep.getProcess(), variables);
                } catch (Exception e) {
                    LOGGER.info("Could not start process", e);

                    BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest = new ActivitiLogBasedProcessInstanceOutcomeChecker(
                            logFile).checkProcessOutcome(bpmnProcess.getName());
                    if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.RUNTIME) {
                        BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_RUNTIME);
                    } else if (outcomeAfterTest
                            == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                        BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
                    }
                }
            } else if (testStep instanceof DelayTestStep) {
                WaitTasks.sleep(((DelayTestStep) testStep).getTimeToWaitAfterwards());
            } else if (testStep instanceof GatherTracesTestStep) {
                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest = new ActivitiLogBasedProcessInstanceOutcomeChecker(
                        logFile).checkProcessOutcome(bpmnProcess.getName());
                if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.RUNTIME) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_RUNTIME);
                } else if (outcomeAfterTest
                        == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
                }

                // Check on parallel execution
                BPMNEnginesUtil.checkParallelExecution(testCase, instanceLogFile);

                // Check whether MARKER file exists
                BPMNEnginesUtil.checkMarkerFileExists(testCase, instanceLogFile);

                // Check data type
                BPMNEnginesUtil.checkDataLog(testCase, instanceLogFile);

                BPMNEnginesUtil.substituteSpecificErrorsForGenericError(testCase, instanceLogFile);
            }

        }

        LOGGER.info("contents of log file " + instanceLogFile + ": " + FileTasks.readAllLines(instanceLogFile));

        bpmnTester.test();
    }

}
