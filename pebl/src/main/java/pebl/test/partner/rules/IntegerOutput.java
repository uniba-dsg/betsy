package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class IntegerOutput extends Output {

    @XmlElement(required = true)
    private final int value;

    IntegerOutput() {
        this(Integer.MIN_VALUE);
    }

    public IntegerOutput(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
