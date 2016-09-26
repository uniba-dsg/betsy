package pebl.test.assertions;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Trace {

    @XmlElement(required = true)
    private final String value;

    Trace() {
        this("");
    }

    public Trace(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Trace trace = (Trace) o;
        return Objects.equals(value, trace.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
