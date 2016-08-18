package betsy.bpmn.model;

import betsy.common.model.input.AssertableTestStep;

import java.util.Optional;

public class BPMNTestStep extends AssertableTestStep {

    private Optional<BPMNTestInput> input = Optional.empty();

    private Optional<Integer> delay = Optional.empty();

    public void setInput(BPMNTestInput input) {
        this.input = Optional.of(input);
    }

    public BPMNTestStep addAssertions(BPMNAssertions assertion) {
        getAssertions().add(new BPMNTestAssertion(assertion));

        return this;
    }


    public void setDelay(int delay) {
        this.delay = Optional.of(delay);
    }

    public Optional<Integer> getDelay() {
        return delay;
    }

    public Optional<Variable> getVariable() {
        if (input.isPresent()) {
            return Optional.of(input.get().getVariable());
        } else {
            return Optional.empty();
        }
    }
}
