package pebl.test.assertions;

import java.util.Objects;

import pebl.test.TestAssertion;

public class TraceTestAssertion extends TestAssertion{

    private final Trace trace;

    public TraceTestAssertion(Trace trace) {
        this.trace = Objects.requireNonNull(trace);
    }

    public Trace getTrace() {
        return trace;
    }
}
