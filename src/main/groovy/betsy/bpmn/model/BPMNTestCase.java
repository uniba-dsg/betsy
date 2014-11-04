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

    private BPMNTestCase addAssertions(BPMNAssertion assertion) {
        assertions.add(assertion.toString());
        return this;
    }

    public BPMNTestCase assertTask1() {
        return addAssertions(BPMNAssertion.SCRIPT_task1);
    }

    public BPMNTestCase assertTask2() {
        return addAssertions(BPMNAssertion.SCRIPT_task2);
    }

    public BPMNTestCase assertTask3() {
        return addAssertions(BPMNAssertion.SCRIPT_task3);
    }

    public BPMNTestCase assertTask4() {
        return addAssertions(BPMNAssertion.SCRIPT_task4);
    }

    public BPMNTestCase assertTask5() {
        return addAssertions(BPMNAssertion.SCRIPT_task5);
    }

    public BPMNTestCase assertRuntimeException() {
        return addAssertions(BPMNAssertion.ERROR_RUNTIME);
    }

    public BPMNTestCase assertErrorThrownErrorEvent() {
        return addAssertions(BPMNAssertion.ERROR_THROWN_ERROR_EVENT);
    }

    public BPMNTestCase assertErrorThrownEscalationEvent() {
        return addAssertions(BPMNAssertion.ERROR_THROWN_ESCALATION_EVENT);
    }

    public BPMNTestCase optionDelay(int delay) {
        this.delay = delay;
        return this;
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
    private int delay = 0;
    private List<BPMNTestCaseVariable> variables = new LinkedList<>();
}
