package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class IntegerInput extends Input {

    @XmlElement(required = true)
    private final int value;

    IntegerInput() {
        this(Integer.MIN_VALUE);
    }

    public IntegerInput(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
