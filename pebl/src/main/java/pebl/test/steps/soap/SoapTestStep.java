package pebl.test.steps.soap;

import pebl.test.assertions.XpathTestAssertion;
import pebl.test.steps.AssertableTestStep;

public class SoapTestStep extends AssertableTestStep {

    /**
     * The input value which is send using the <code>operation</code> to the system under test.
     */
    private String input;

    /**
     * The WSDL operation which is invoked in this test step
     */
    private WsdlOperation operation;

    private WsdlService service = new WsdlService("testInterface");

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
