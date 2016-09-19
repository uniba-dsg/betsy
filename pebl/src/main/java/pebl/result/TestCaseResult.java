package pebl.result;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class TestCaseResult {

    private final String name;
    private final int number;
    private final String message;

    TestCaseResult() {
        this("", Integer.MIN_VALUE, "");
    }

    public TestCaseResult(String name, int number, String message) {
        this.name = Objects.requireNonNull(name);
        this.number = Objects.requireNonNull(number);
        this.message = Objects.requireNonNull(message);
    }

    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    @XmlElement(required = true)
    public int getNumber() {
        return number;
    }

    @XmlElement(required = true)
    public String getMessage() {
        return message;
    }
}
