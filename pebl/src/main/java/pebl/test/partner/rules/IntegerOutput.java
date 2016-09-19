package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlElement;

public class IntegerOutput extends Output {

    private final int value;

    IntegerOutput() {
        this(Integer.MIN_VALUE);
    }

    public IntegerOutput(int value) {
        this.value = value;
    }

    @XmlElement(required = true)
    public int getValue() {
        return value;
    }
}
