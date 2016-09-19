package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlElement;

public class FaultOutput extends Output {

    private final FaultVariant variant;

    FaultOutput() {
        this(null);
    }

    public FaultOutput(FaultVariant variant) {
        this.variant = variant;
    }

    @XmlElement(required = true)
    public FaultVariant getVariant() {
        return variant;
    }
}
