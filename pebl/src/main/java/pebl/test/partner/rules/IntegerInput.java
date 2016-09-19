package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlElement;

public class IntegerInput extends Input {

    private final int value;

    IntegerInput() {
        this(Integer.MIN_VALUE);
    }

    public IntegerInput(int value) {
        this.value = value;
    }

    @XmlElement(required = true)
    public int getValue() {
        return value;
    }
}
