package pebl.test.assertions;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class Trace {

    private final String value;

    Trace() {
        this("");
    }

    public Trace(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @XmlElement(required = true)
    public String getValue() {
        return value;
    }
}
