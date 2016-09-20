package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class FaultOutput extends Output {

    @XmlAnyElement
    private final Object variant;

    FaultOutput() {
        this(null);
    }

    public FaultOutput(Object variant) {
        this.variant = variant;
    }

    public Object getVariant() {
        return variant;
    }
}
