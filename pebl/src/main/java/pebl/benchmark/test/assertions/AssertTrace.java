package pebl.benchmark.test.assertions;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.benchmark.test.TestAssertion;

@XmlAccessorType(XmlAccessType.NONE)
public class AssertTrace extends TestAssertion{

    @XmlElement(required = true)
    private final Trace trace;

    AssertTrace() {
        this(new Trace());
    }

    public AssertTrace(Trace trace) {
        this.trace = Objects.requireNonNull(trace);
    }

    public Trace getTrace() {
        return trace;
    }
}
