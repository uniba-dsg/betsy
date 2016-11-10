package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class SoapMessageOutput extends HttpOutput {

    SoapMessageOutput() {
        this("", 200);
    }

    // TODO convert to soap/body
    public SoapMessageOutput(int v, int statusCode) {
        this(String.valueOf(v), statusCode);
    }

    public SoapMessageOutput(String soapMessage, int statusCode) {
        super(statusCode, "application/soap+xml", soapMessage);
    }

}
