package pebl.test.steps;

import java.util.Optional;

import pebl.test.assertions.Trace;
import pebl.test.assertions.TraceTestAssertion;

public class VariableBasedTestStep extends AssertableTestStep {

    private Optional<Variable> variable = Optional.empty();

    private Optional<Integer> delay = Optional.empty();

    public void setVariable(Variable variable) {
        this.variable = Optional.of(variable);
    }

    public VariableBasedTestStep addAssertions(Trace trace) {
        getAssertions().add(new TraceTestAssertion(trace));

        return this;
    }

    public void setDelay(int delay) {
        this.delay = Optional.of(delay);
    }

    public Optional<Integer> getDelay() {
        return delay;
    }

    public Optional<Variable> getVariable() {
        if (variable.isPresent()) {
            return Optional.of(variable.get());
        } else {
            return Optional.empty();
        }
    }
}
