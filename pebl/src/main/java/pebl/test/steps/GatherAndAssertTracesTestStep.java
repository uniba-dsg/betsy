package pebl.test.steps;

import pebl.test.assertions.Trace;
import pebl.test.assertions.TraceTestAssertion;

public class GatherAndAssertTracesTestStep extends AssertableTestStep {

    public GatherAndAssertTracesTestStep addTrace(Trace trace) {
        getAssertions().add(new TraceTestAssertion(trace));

        return this;
    }

}
