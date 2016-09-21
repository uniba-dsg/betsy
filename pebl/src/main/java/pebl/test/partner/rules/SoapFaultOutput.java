package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
public class SoapFaultOutput extends Output {

    @XmlCDATA
    private final String variant;

    SoapFaultOutput() {
        this(null);
    }

    public SoapFaultOutput(String variant) {
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }
}
