package betsy.data.steps

import betsy.data.TestAssertion
import betsy.data.TestStep
import betsy.data.assertions.XpathTestAssertion

class SoapTestStep extends TestStep {

    /**
     * List of assertions which are evaluated after the test step has been executed/the messages have been sent.
     */
    List<TestAssertion> assertions = []

    /**
     * The input value which is send using the <code>operation</code> to the system under test.
     */
    String input

    /**
     * The WSDL operation which is invoked in this test step
     */
    WsdlOperation operation

    boolean isOneWay() {
        WsdlOperation.ASYNC == operation
    }

    boolean testPartner = false
    boolean concurrencyTest = false

    public void setOutput(String output) {
        assertions << new XpathTestAssertion(expectedOutput: output, xpathExpression: "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';number(//test:testElementSyncResponse) cast as xs:integer", output: output)
    }

    public void setConcurrencyTest(boolean concurrencyTest) {
        if (concurrencyTest) {
            assertions << new XpathTestAssertion(expectedOutput: "true", xpathExpression: "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncResponse > 0")
        }
        this.concurrencyTest = concurrencyTest
    }

    public void setPartnerOutput(String output) {
        assertions << new XpathTestAssertion(expectedOutput: output, xpathExpression: "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';number(//test:testElementSyncResponse) cast as xs:integer", output: output)
    }

    public void setStringOperationOutput(String output) {
        assertions << new XpathTestAssertion(expectedOutput: output, xpathExpression: "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';//test:testElementSyncStringResponse", output: output)
    }

    public void setOutputAsLeast(String output) {
        assertions << new XpathTestAssertion(expectedOutput: "true", xpathExpression: "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';//test:testElementSyncResponse >= ${output}", output: "${output}")
    }

    @Override
    public String toString() {
        return super.toString() + "SoapTestStep{" +
                "input='" + input + '\'' +
                ", operation=" + operation +
                ", testPartner=" + testPartner +
                ", concurrencyTest=" + concurrencyTest +
                ", assertions=" + assertions +
                '}';
    }
}
