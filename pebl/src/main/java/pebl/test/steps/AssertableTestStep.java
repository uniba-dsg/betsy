package pebl.test.steps;

import java.util.ArrayList;
import java.util.List;

import pebl.test.TestAssertion;
import pebl.test.TestStep;

public class AssertableTestStep extends TestStep {

    public List<TestAssertion> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<TestAssertion> assertions) {
        this.assertions = assertions;
    }

    /**
     * List of assertions which are evaluated after the test step has been executed/the messages have been sent.
     */
    private List<TestAssertion> assertions = new ArrayList<>();

    @Override
    public String toString() {
        return "AssertableTestStep{" +
                "assertions=" + assertions +
                "} " + super.toString();
    }
}
