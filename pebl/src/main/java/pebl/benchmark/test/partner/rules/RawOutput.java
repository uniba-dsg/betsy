package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class RawOutput extends Output {

    @XmlElement(required = true)
    private final String value;

    RawOutput() {
        this("");
    }

    public RawOutput(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
