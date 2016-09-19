package pebl.test.assertions;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestAssertion;

public class TraceTestAssertion extends TestAssertion{

    private final Trace trace;

    TraceTestAssertion() {
        this(new Trace());
    }

    public TraceTestAssertion(Trace trace) {
        this.trace = Objects.requireNonNull(trace);
    }

    @XmlElement(required = true)
    public Trace getTrace() {
        return trace;
    }
}
