package pebl.result.test;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public class TestCaseResult {

    @XmlAttribute(required = true)
    private final String name;

    @XmlAttribute(required = true)
    private final int number;

    @XmlValue
    private final String message;

    TestCaseResult() {
        this("", Integer.MIN_VALUE, "");
    }

    public TestCaseResult(String name, int number, String message) {
        this.name = Objects.requireNonNull(name);
        this.number = Objects.requireNonNull(number);
        this.message = Objects.requireNonNull(message);
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }
}
