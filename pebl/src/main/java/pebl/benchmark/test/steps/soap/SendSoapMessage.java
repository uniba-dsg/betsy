package pebl.benchmark.test.steps.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.benchmark.test.TestStep;

@XmlAccessorType(XmlAccessType.NONE)
public class SendSoapMessage extends TestStep {

    /**
     * The input value which is send using the <code>operation</code> to the system under test.
     */
    @XmlElement(required = true)
    private String soapMessage;

    /**
     * The WSDL operation which is invoked in this test step
     */
    @XmlElement(required = true)
    private WsdlOperation operation;

    @XmlElement(required = true)
    private WsdlService service;

    @Override
    public String toString() {
        return "SoapTestStep{" +
                "input='" + soapMessage + '\'' +
                ", operation=" + operation +
                ", service=" + service +
                "} " + super.toString();
    }

    public String getSoapMessage() {
        return soapMessage;
    }

    public void setSoapMessage(String soapMessage) {
        this.soapMessage = soapMessage;
    }

    public WsdlOperation getOperation() {
        return operation;
    }

    public void setOperation(WsdlOperation operation) {
        this.operation = operation;
    }

    public void setService(WsdlService service) {
        this.service = service;
    }

    public WsdlService getService() {
        return service;
    }
}
