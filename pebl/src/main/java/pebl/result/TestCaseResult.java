package pebl.result;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class TestCaseResult {

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final int number;

    @XmlElement(required = true)
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
