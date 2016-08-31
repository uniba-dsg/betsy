package betsy.bpmn.model;

import java.util.Objects;

import betsy.common.model.input.TestAssertion;

public class BPMNTestAssertion extends TestAssertion{

    private final BPMNAssertions assertion;

    public BPMNTestAssertion(BPMNAssertions assertion) {
        this.assertion = Objects.requireNonNull(assertion);
    }

    public BPMNAssertions getAssertion() {
        return assertion;
    }
}
