package pebl.test.steps.soap;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestStep;

public class SoapTestStep extends TestStep {

    /**
     * The input value which is send using the <code>operation</code> to the system under test.
     */
    private String input;

    /**
     * The WSDL operation which is invoked in this test step
     */
    private WsdlOperation operation;

    private WsdlService service;

    @Override
    public String toString() {
        return "SoapTestStep{" +
                "input='" + input + '\'' +
                ", operation=" + operation +
                ", service=" + service +
                "} " + super.toString();
    }

    @XmlElement(required = true)
    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @XmlElement(required = true)
    public WsdlOperation getOperation() {
        return operation;
    }

    public void setOperation(WsdlOperation operation) {
        this.operation = operation;
    }

    public void setService(WsdlService service) {
        this.service = service;
    }

    @XmlElement(required = true)
    public WsdlService getService() {
        return service;
    }
}
