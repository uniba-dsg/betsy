package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class SoapMessageInput extends AnyInput {

    @XmlValue
    @XmlCDATA
    private final String soapMessage;

    SoapMessageInput() {
        this(Integer.MIN_VALUE);
    }

    // TODO change constructor to soap message values
    public SoapMessageInput(int soapMessage) {
        this.soapMessage = String.valueOf(soapMessage);
    }

    public String getSoapMessage() {
        return soapMessage;
    }
}
