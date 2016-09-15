package betsy.bpmn.model;

import java.util.LinkedList;
import java.util.List;

import pebl.test.TestCase;
import pebl.test.assertions.Trace;
import pebl.test.steps.DelayTestStep;
import pebl.test.steps.GatherAndAssertTracesTestStep;
import pebl.test.steps.vars.ProcessStartWithVariablesTestStep;
import pebl.test.steps.vars.Variable;

public class BPMNTestCaseBuilder {

    public static final String PARALLEL_PROCESS_KEY = "ParallelProcess";

    public TestCase getTestCase(int number, String key) {
        TestCase result = new TestCase();
        if(isParallel) {
            // add step that starts the parallel process
            ProcessStartWithVariablesTestStep parallelStart = new ProcessStartWithVariablesTestStep();
            parallelStart.setProcess(PARALLEL_PROCESS_KEY);
            result.addStep(parallelStart);
        }

        // add step that starts the real process
        ProcessStartWithVariablesTestStep processStartWithVariablesTestStep = new ProcessStartWithVariablesTestStep();
        processStartWithVariablesTestStep.setProcess(key);
        if(input != null) {
            processStartWithVariablesTestStep.addVariable(input);
        }
        processStartWithVariablesTestStep.addVariable(new Variable("integerVariable", "Integer", integerVariable));
        processStartWithVariablesTestStep.addVariable(new Variable("testCaseNumber", "Integer", number));
        result.addStep(processStartWithVariablesTestStep);

        // add delay
        DelayTestStep delayTestStep = new DelayTestStep();
        delayTestStep.setTimeToWaitAfterwards(delay);
        result.addStep(delayTestStep);

        // add trace gathering step

        // add trace evaluation step
        GatherAndAssertTracesTestStep gatherAndAssertTracesTestStep = new GatherAndAssertTracesTestStep();
        traces.forEach(gatherAndAssertTracesTestStep::addTrace);
        result.addStep(gatherAndAssertTracesTestStep);

        return result;
    }

    private List<Trace> traces = new LinkedList<>();
    private boolean isParallel = false;
    private int delay = 0;
    private Integer integerVariable = 0;
    private Variable input = null;

    private BPMNTestCaseBuilder addInputTestString(BPMNTestInput value) {
        input = value.getVariable();

        return this;
    }

    public BPMNTestCaseBuilder inputA() {
        return addInputTestString(BPMNTestInput.INPUT_A);
    }

    public BPMNTestCaseBuilder inputB() {
        return addInputTestString(BPMNTestInput.INPUT_B);
    }

    public BPMNTestCaseBuilder inputC() {
        return addInputTestString(BPMNTestInput.INPUT_C);
    }

    public BPMNTestCaseBuilder inputAB() {
        return addInputTestString(BPMNTestInput.INPUT_AB);
    }

    public BPMNTestCaseBuilder inputAC() {
        return addInputTestString(BPMNTestInput.INPUT_AC);
    }

    public BPMNTestCaseBuilder inputBC() {
        return addInputTestString(BPMNTestInput.INPUT_BC);
    }

    public BPMNTestCaseBuilder inputABC() {
        return addInputTestString(BPMNTestInput.INPUT_ABC);
    }

    public BPMNTestCaseBuilder assertTask1() {
        return addAssertion(BPMNAssertions.SCRIPT_task1);
    }

    private BPMNTestCaseBuilder addAssertion(BPMNAssertions bpmnAssertions) {
        this.traces.add(new Trace(bpmnAssertions.toString()));

        return this;
    }

    public BPMNTestCaseBuilder assertTask2() {
        return addAssertion(BPMNAssertions.SCRIPT_task2);
    }

    public BPMNTestCaseBuilder assertTask3() {
        return addAssertion(BPMNAssertions.SCRIPT_task3);
    }

    public BPMNTestCaseBuilder assertTask4() {
        return addAssertion(BPMNAssertions.SCRIPT_task4);
    }

    public BPMNTestCaseBuilder assertTask5() {
        return addAssertion(BPMNAssertions.SCRIPT_task5);
    }

    public BPMNTestCaseBuilder assertMarkerExists() {
        return addAssertion(BPMNAssertions.MARKER_EXISTS);
    }

    public BPMNTestCaseBuilder assertIncrement() {
        return addAssertion(BPMNAssertions.INCREMENT);
    }

    public BPMNTestCaseBuilder useParallelProcess() {
        this.isParallel = true;

        return this;
    }

    public BPMNTestCaseBuilder setIntegerVariable(int value) {
        this.integerVariable = value;

        return this;
    }

    public BPMNTestCaseBuilder optionDelay(int delay) {
        this.delay = delay;

        return this;
    }

    public BPMNTestCaseBuilder assertRuntimeException() {
        return addAssertion(BPMNAssertions.ERROR_RUNTIME);
    }

    public BPMNTestCaseBuilder assertErrorThrownErrorEvent() {
        return addAssertion(BPMNAssertions.ERROR_THROWN_ERROR_EVENT);
    }

    public BPMNTestCaseBuilder assertErrorThrownEscalationEvent() {
        return addAssertion(BPMNAssertions.ERROR_THROWN_ESCALATION_EVENT);
    }

    public BPMNTestCaseBuilder assertGenericError() {
        return addAssertion(BPMNAssertions.ERROR_GENERIC);
    }

    public BPMNTestCaseBuilder assertExecutionParallel() {
        return addAssertion(BPMNAssertions.EXECUTION_PARALLEL);
    }

    public BPMNTestCaseBuilder assertDataCorrect() {
        return addAssertion(BPMNAssertions.DATA_CORRECT);
    }

}
