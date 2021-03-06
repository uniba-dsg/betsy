package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class SoapMessageOutput extends HttpOutput {

    SoapMessageOutput() {
        this("", 200);
    }

    public SoapMessageOutput(int v, int statusCode) {
        this(String.valueOf(v), statusCode);
    }

    public SoapMessageOutput(String soapMessage, int statusCode) {
        super(statusCode, "application/soap+xml", soapMessage);
    }

}
