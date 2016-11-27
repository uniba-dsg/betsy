package betsy.bpmn.engines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import org.apache.log4j.Logger;
import pebl.benchmark.test.TestCase;
import pebl.benchmark.test.TestStep;
import pebl.benchmark.test.assertions.AssertTrace;
import pebl.benchmark.test.steps.CheckDeployment;
import pebl.benchmark.test.steps.DelayTesting;
import pebl.benchmark.test.steps.GatherTraces;
import pebl.benchmark.test.steps.vars.StartProcess;
import pebl.benchmark.test.steps.vars.Variable;

public class GenericBPMNTester {

    private static final Logger LOGGER = Logger.getLogger(GenericBPMNTester.class);

    private final BPMNProcess bpmnProcess;
    private final TestCase testCase;
    private final Path instanceLogFile;
    private final BPMNTester bpmnTester;
    private final BPMNProcessInstanceOutcomeChecker checkerOutcomeAfter;
    private final BPMNProcessStarter processStarter;
    private final BPMNProcessInstanceOutcomeChecker checkerOutcomeBefore;

    public GenericBPMNTester(BPMNProcess bpmnProcess, TestCase testCase, Path instanceLogFile,
            BPMNTester bpmnTester,
            BPMNProcessInstanceOutcomeChecker checkerOutcomeBefore,
            BPMNProcessInstanceOutcomeChecker checkerOutcomeAfter,
            BPMNProcessStarter processStarter) {
        this.bpmnProcess = bpmnProcess;
        this.testCase = Objects.requireNonNull(testCase);
        this.instanceLogFile = Objects.requireNonNull(instanceLogFile);
        this.bpmnTester = Objects.requireNonNull(bpmnTester);
        this.checkerOutcomeBefore = Objects.requireNonNull(checkerOutcomeBefore);
        this.checkerOutcomeAfter = Objects.requireNonNull(checkerOutcomeAfter);
        this.processStarter = Objects.requireNonNull(processStarter);
    }


    /**
     * runs a single test
     */
    public void runTest() {

        for (TestStep testStep : testCase.getTestSteps()) {
            if (testStep instanceof CheckDeployment) {
                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeBeforeTest = checkerOutcomeBefore
                        .checkProcessOutcome(bpmnProcess.getName());
                if (outcomeBeforeTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_DEPLOYMENT);
                }
            } else if (testStep instanceof StartProcess) {
                try {
                    StartProcess processStartWithVariablesTestStep = (StartProcess) testStep;
                    List<Variable> variables = processStartWithVariablesTestStep.getVariables();
                    processStarter.start(processStartWithVariablesTestStep.getProcessName(), variables);
                } catch (Exception e) {
                    LOGGER.info("Could not start process", e);
                    BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest = checkerOutcomeAfter.checkProcessOutcome(bpmnProcess.getName());
                    if (outcomeAfterTest
                            == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                        // do not log at this time
                    } else {
                        BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_RUNTIME);
                    }
                }
            } else if (testStep instanceof DelayTesting) {
                WaitTasks.sleep(((DelayTesting) testStep).getMilliseconds());
            } else if (testStep instanceof GatherTraces) {

                if(shouldDeploymentFail(testStep)) {
                    // Skip further processing if process is expected to be undeployed
                    break;
                }

                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest = checkerOutcomeAfter.checkProcessOutcome(bpmnProcess.getName());
                if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.RUNTIME) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_RUNTIME);
                } else if (outcomeAfterTest
                        == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
                } else if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_PROCESS_ABORTED);
                } else if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ERROR_EVENT_THROWN) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
                } else if (outcomeAfterTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.PROCESS_INSTANCE_ABORTED_BECAUSE_ESCALATION_EVENT_THROWN) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
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

    private boolean shouldDeploymentFail(TestStep testStep) {
        if(testStep.getTestAssertions().stream().filter(a -> a instanceof AssertTrace && ((AssertTrace) a).getTrace().equals(
                BPMNAssertions.ERROR_DEPLOYMENT.toString()))
                .findFirst().isPresent()) {
            // Ensure existence of instanceLogFile for JUnit test execution
            if(!Files.exists(instanceLogFile)) {
                try {
                    Files.createFile(instanceLogFile);
                } catch (IOException e) {
                    LOGGER.warn("Creation of log file failed.", e);
                }
            }
            return true;
        }

        return false;
    }
}
