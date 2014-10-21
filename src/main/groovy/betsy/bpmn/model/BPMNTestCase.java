package betsy.bpmn.model;

import betsy.bpmn.engines.Errors;
import betsy.common.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BPMNTestCase {
    public BPMNTestCase() {
        this(1);
    }

    public BPMNTestCase(int number) {
        this.number = number;
        initializeTestCaseNumber();
    }

    private void initializeTestCaseNumber() {
        variables.add(new BPMNTestCaseVariable("testCaseNumber", "Integer", number));
    }

    private BPMNTestCase addInputTestString(String value) {
        variables.add(new BPMNTestCaseVariable("test", "String", value));

        return this;
    }

    public BPMNTestCase inputA() {
        return addInputTestString("a");
    }

    public BPMNTestCase inputB() {
        return addInputTestString("b");
    }

    public BPMNTestCase inputAB() {
        return addInputTestString("ab");
    }

    public BPMNTestCase inputC() {
        return addInputTestString("c");
    }

    private BPMNTestCase addAssertions(String assertion) {
        assertions.add(assertion);
        return this;
    }

    public BPMNTestCase assertSuccess() {
        return addAssertions("SCRIPT_success");
    }

    public BPMNTestCase assertTask1() {
        return addAssertions("SCRIPT_task1");
    }

    public BPMNTestCase assertTask2() {
        return addAssertions("SCRIPT_task2");
    }

    public BPMNTestCase assertTask3() {
        return addAssertions("SCRIPT_task3");
    }

    public BPMNTestCase assertTrue() {
        return addAssertions("SCRIPT_true");
    }

    public BPMNTestCase assertFalse() {
        return addAssertions("SCRIPT_false");
    }

    public BPMNTestCase assertDefault() {
        return addAssertions("SCRIPT_default");
    }

    public BPMNTestCase assertRuntimeException() {
        return addAssertions(Errors.ERROR_RUNTIME);
    }

    public BPMNTestCase assertErrorThrownErrorEvent() {
        return addAssertions(Errors.ERROR_THROWN_ERROR_EVENT);
    }

    public BPMNTestCase assertSubprocess() {
        return addAssertions("SCRIPT_subprocess");
    }

    public BPMNTestCase assertInstanceExecution() {
        return addAssertions("SCRIPT_taskInstanceExecuted");
    }

    public BPMNTestCase assertInterrupted() {
        return addAssertions("SCRIPT_interrupted");
    }

    public BPMNTestCase assertSignaled() {
        return addAssertions("SCRIPT_signaled");
    }

    public BPMNTestCase assertNormalTask() {
        return addAssertions("SCRIPT_normalTask");
    }

    public BPMNTestCase assertTransactionTask() {
        return addAssertions("SCRIPT_transaction");
    }

    public BPMNTestCase assertCompensate() {
        return addAssertions("SCRIPT_compensate");
    }

    public BPMNTestCase assertNotInterrupted() {
        return addAssertions("SCRIPT_taskNotInterrupted");
    }

    public BPMNTestCase assertStarted() {
        return addAssertions("SCRIPT_started");
    }

    public BPMNTestCase assertTimerInternal() {
        return addAssertions("SCRIPT_timerInternal");
    }

    public BPMNTestCase assertTimerExternal() {
        return addAssertions("SCRIPT_timerExternal");
    }

    public BPMNTestCase assertTimerEvent() {
        return addAssertions("SCRIPT_timerEvent");
    }

    public BPMNTestCase assertCondition() {
        return addAssertions("SCRIPT_condition");
    }

    public BPMNTestCase optionDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public BPMNTestCase optionSelfStarting() {
        selfStarting = true;
        return this;
    }

    public boolean getSelfStarting() {
        return selfStarting;
    }

    public int getNumber() {
        return number;
    }

    public int getDelay() {
        return delay;
    }

    public List<String> getAssertions() {
        return assertions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("test").append(number).append("Assert");
        for (String assertion : assertions) {
            sb.append(StringUtils.capitalize(assertion));
        }
        return sb.toString();
    }

    public List<BPMNTestCaseVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<BPMNTestCaseVariable> variables) {
        this.variables = variables;
    }

    private int number;
    private List<String> assertions = new ArrayList<>();
    private boolean selfStarting = false;
    private int delay = 0;
    private List<BPMNTestCaseVariable> variables = new LinkedList<>();
}
