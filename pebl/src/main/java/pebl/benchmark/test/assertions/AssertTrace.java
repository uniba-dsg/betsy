package pebl.benchmark.test.assertions;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.benchmark.test.TestAssertion;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class AssertTrace extends TestAssertion{

    @XmlAttribute(required = true)
    private final String trace;

    AssertTrace() {
        this("");
    }

    public AssertTrace(String trace) {
        this.trace = Objects.requireNonNull(trace);
    }

    public String getTrace() {
        return trace;
    }
}
