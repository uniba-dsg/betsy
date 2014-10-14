package betsy.bpmn.model;

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
        return addAssertions("success");
    }

    public BPMNTestCase assertTask1() {
        return addAssertions("task1");
    }

    public BPMNTestCase assertTask2() {
        return addAssertions("task2");
    }

    public BPMNTestCase assertTrue() {
        return addAssertions("true");
    }

    public BPMNTestCase assertFalse() {
        return addAssertions("false");
    }

    public BPMNTestCase assertDefault() {
        return addAssertions("default");
    }

    public BPMNTestCase assertRuntimeException() {
        return addAssertions("runtimeException");
    }

    public BPMNTestCase assertThrownErrorEvent() {
        return addAssertions("thrownErrorEvent");
    }

    public BPMNTestCase assertSubprocess() {
        return addAssertions("subprocess");
    }

    public BPMNTestCase assertLane1() {
        return addAssertions("lane1");
    }

    public BPMNTestCase assertLane2() {
        return addAssertions("lane2");
    }

    public BPMNTestCase assertInstanceExecution() {
        return addAssertions("taskInstanceExecuted");
    }

    public BPMNTestCase assertInterrupted() {
        return addAssertions("interrupted");
    }

    public BPMNTestCase assertSignaled() {
        return addAssertions("signaled");
    }

    public BPMNTestCase assertNormalTask() {
        return addAssertions("normalTask");
    }

    public BPMNTestCase assertErrorTask() {
        return addAssertions("errorTask");
    }

    public BPMNTestCase assertTransactionTask() {
        return addAssertions("transaction");
    }

    public BPMNTestCase assertCompensate() {
        return addAssertions("compensate");
    }

    public BPMNTestCase assertNotInterrupted() {
        return addAssertions("taskNotInterrupted");
    }

    public BPMNTestCase assertStarted() {
        return addAssertions("started");
    }

    public BPMNTestCase assertTimerInternal() {
        return addAssertions("timerInternal");
    }

    public BPMNTestCase assertTimerExternal() {
        return addAssertions("timerExternal");
    }

    public BPMNTestCase assertTimerEvent() {
        return addAssertions("timerEvent");
    }

    public BPMNTestCase assertCallableElementExecuted() {
        return addAssertions("callableElementExecuted");
    }

    public BPMNTestCase assertCondition() {
        return addAssertions("condition");
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
