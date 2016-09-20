package pebl.test.steps.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestStep;

@XmlAccessorType(XmlAccessType.NONE)
public class SoapTestStep extends TestStep {

    /**
     * The input value which is send using the <code>operation</code> to the system under test.
     */
    @XmlElement(required = true)
    private String input;

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
                "input='" + input + '\'' +
                ", operation=" + operation +
                ", service=" + service +
                "} " + super.toString();
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
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
