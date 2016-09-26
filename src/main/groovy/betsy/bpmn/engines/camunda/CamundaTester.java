package betsy.bpmn.engines.camunda;

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
import pebl.test.Test;
import pebl.test.TestAssertion;
import pebl.test.TestCase;
import pebl.test.TestStep;
import pebl.test.assertions.Trace;
import pebl.test.assertions.TraceTestAssertion;
import pebl.test.steps.DelayTestStep;
import pebl.test.steps.DeployableCheckTestStep;
import pebl.test.steps.GatherTracesTestStep;
import pebl.test.steps.vars.ProcessStartWithVariablesTestStep;
import pebl.test.steps.vars.Variable;

public class CamundaTester {

    private static final Logger LOGGER = Logger.getLogger(CamundaTester.class);

    private final BPMNProcess bpmnProcess;
    private final TestCase testCase;
    private final Path logDir;
    private final Path instanceLogFile;
    private final BPMNTester bpmnTester;

    public CamundaTester(BPMNProcess bpmnProcess, TestCase testCase, Path logDir, Path instanceLogFile, BPMNTester bpmnTester) {
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

        Path logFile = FileTasks.findFirstMatchInFolder(logDir, "catalina*");

        for (TestStep testStep : testCase.getTestSteps()) {
            if (testStep instanceof DeployableCheckTestStep) {
                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeBeforeTest = new CamundaApiBasedProcessInstanceOutcomeChecker()
                        .checkProcessOutcome(bpmnProcess.getName());
                if (outcomeBeforeTest == BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome.UNDEPLOYED_PROCESS) {
                    BPMNAssertions.appendToFile(instanceLogFile, BPMNAssertions.ERROR_DEPLOYMENT);
                }
            } else if (testStep instanceof ProcessStartWithVariablesTestStep) {
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
                // Skip further processing if process is expected to be undeployed
                if(testStep.getAssertions().stream().filter(a -> a instanceof TraceTestAssertion && ((TraceTestAssertion) a).getTrace().equals(new Trace(BPMNAssertions.ERROR_DEPLOYMENT.toString())))
                        .findFirst().isPresent()) {
                    if(!Files.exists(instanceLogFile)) {
                        try {
                            Files.createFile(instanceLogFile);
                        } catch (IOException e) {
                            LOGGER.warn("Creation of log file failed.", e);
                        }
                    }
                    break;
                }
                BPMNProcessInstanceOutcomeChecker.ProcessInstanceOutcome outcomeAfterTest = new CamundaLogBasedProcessInstanceOutcomeChecker(
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
