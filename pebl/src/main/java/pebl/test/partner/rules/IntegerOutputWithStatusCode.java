package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class IntegerOutputWithStatusCode extends IntegerOutput {

    @XmlElement(required = true)
    private final int statusCode;

    IntegerOutputWithStatusCode() {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public IntegerOutputWithStatusCode(int value, int statusCode) {
        super(value);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
