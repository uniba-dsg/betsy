package betsy.bpel.model.steps;

import betsy.bpel.model.assertions.XpathTestAssertion;
import betsy.common.model.AssertableTestStep;

public class SoapTestStep extends AssertableTestStep {

    /**
     * The input value which is send using the <code>operation</code> to the system under test.
     */
    private String input;
    /**
     * The WSDL operation which is invoked in this test step
     */
    private WsdlOperation operation;
    private boolean testPartner = false;
    private boolean concurrencyTest = false;

    public boolean isOneWay() {
        return WsdlOperation.ASYNC.equals(operation);
    }

    public void setOutput(String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput(output);
        assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';number(//test:testElementSyncResponse) cast as xs:integer");
        getAssertions().add(assertion);
    }

    public void setPartnerOutput(String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput(output);
        assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';number(//test:testElementSyncResponse) cast as xs:integer");
        getAssertions().add(assertion);
    }

    public void setStringOperationOutput(String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput(output);
        assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';//test:testElementSyncStringResponse");
        getAssertions().add(assertion);
    }

    public void setOutputAsLeast(final String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput("true");
        assertion.setXpathExpression("declare namespace test=\'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\';//test:testElementSyncResponse >= " + output);
        getAssertions().add(assertion);
    }

    @Override
    public String toString() {
        return "SoapTestStep{" +
                "input='" + input + '\'' +
                ", operation=" + operation +
                ", testPartner=" + testPartner +
                ", concurrencyTest=" + concurrencyTest +
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

    public boolean isTestPartner() {
        return testPartner;
    }

    public void setTestPartner(boolean testPartner) {
        this.testPartner = testPartner;
    }

    public boolean isConcurrencyTest() {
        return concurrencyTest;
    }

    public void setConcurrencyTest(boolean concurrencyTest) {
        if (concurrencyTest) {
            XpathTestAssertion assertion = new XpathTestAssertion();
            assertion.setExpectedOutput("true");
            assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncResponse > 0");
            getAssertions().add(assertion);
        }

        this.concurrencyTest = concurrencyTest;
    }
}
