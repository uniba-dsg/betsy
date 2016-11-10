package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class SoapFaultOutput extends SoapMessageOutput {

    SoapFaultOutput() {

    }

    public SoapFaultOutput(String soapMessage, int statusCode) {
        super(soapMessage, statusCode);
    }

    public SoapFaultOutput(String soapMessage) {
        super(soapMessage, 500);
    }

}
