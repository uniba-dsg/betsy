package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlElement;

public class RawOutput extends Output {

    private final String value;

    RawOutput() {
        this("");
    }

    public RawOutput(String value) {
        this.value = value;
    }

    @XmlElement(required = true)
    public String getValue() {
        return value;
    }
}
