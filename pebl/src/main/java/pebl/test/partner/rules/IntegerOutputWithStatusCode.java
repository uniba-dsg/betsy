package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlElement;

public class IntegerOutputWithStatusCode extends IntegerOutput {

    private final int statusCode;

    IntegerOutputWithStatusCode() {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public IntegerOutputWithStatusCode(int value, int statusCode) {
        super(value);
        this.statusCode = statusCode;
    }

    @XmlElement(required = true)
    public int getStatusCode() {
        return statusCode;
    }
}
