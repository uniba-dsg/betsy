package betsy.bpmn.model;

import betsy.common.model.TestAssertion;

import java.util.Objects;

public class BPMNTestAssertion extends TestAssertion{

    private final BPMNAssertions assertion;

    public BPMNTestAssertion(BPMNAssertions assertion) {
        this.assertion = Objects.requireNonNull(assertion);
    }

    public BPMNAssertions getAssertion() {
        return assertion;
    }
}
