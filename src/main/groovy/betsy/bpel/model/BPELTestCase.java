package betsy.bpel.model;

import pebl.test.TestAssertion;
import pebl.test.TestCase;
import pebl.test.TestStep;
import pebl.test.assertions.XpathTestAssertion;
import pebl.test.steps.*;
import pebl.test.steps.soap.SoapTestStep;
import pebl.test.steps.soap.WsdlService;

import java.util.Objects;

public class BPELTestCase extends TestCase {

    public BPELTestCase() {

    }

    public BPELTestCase(String name) {
        this.setName(Objects.requireNonNull(name));
    }

    public BPELTestCase buildPartnerConcurrencySetup() {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___RESET_COUNTERS));
        step.setService(new WsdlService("testPartner"));

        return addStep(step);
    }

    public BPELTestCase assertConcurrencyAtPartner() {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_CONCURRENT_ACCESS));
        step.setService(new WsdlService("testPartner"));

        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput("true");
        assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncResponse > 0");
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase assertNumberOfPartnerCalls(int value) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_ACCESSES));
        step.setService(new WsdlService("testPartner"));
        setPartnerOutput(step, String.valueOf(value));

        return addStep(step);
    }

    public BPELTestCase checkDeployment() {
        return addStep(new DeployableCheckTestStep());
    }

    public BPELTestCase checkFailedDeployment() {
        return addStep(new NotDeployableCheckTestStep());
    }

    public BPELTestCase sendAsync(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.ASYNC);

        return addStep(step);
    }

    public BPELTestCase buildSyncOperationOutputAsLeast(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        setOutputAsLeast(step, String.valueOf(output));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, String output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        setStringOperationOutput(step, output);
        step.setOperation(BPELWsdlOperations.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        setOutput(step, String.valueOf(output));
        step.setOperation(BPELWsdlOperations.SYNC);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        setOutput(step, String.valueOf(output));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase waitFor(int timeout) {
        DelayTestStep step = new DelayTestStep();
        step.setTimeToWaitAfterwards(timeout);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.SYNC);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase addStep(TestStep step) {
        getTestSteps().add(step);

        return this;
    }

    public void setOutput(SoapTestStep step, String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput(output);
        assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';number(//test:testElementSyncResponse) cast as xs:integer");
        step.getAssertions().add(assertion);
    }

    public void setPartnerOutput(SoapTestStep step, String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput(output);
        assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';number(//test:testElementSyncResponse) cast as xs:integer");
        step.getAssertions().add(assertion);
    }

    public void setStringOperationOutput(SoapTestStep step, String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput(output);
        assertion.setXpathExpression("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';//test:testElementSyncStringResponse");
        step.getAssertions().add(assertion);
    }

    public void setOutputAsLeast(SoapTestStep step, final String output) {
        XpathTestAssertion assertion = new XpathTestAssertion();
        assertion.setExpectedOutput("true");
        assertion.setXpathExpression("declare namespace test=\'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\';//test:testElementSyncResponse >= " + output);
        step.getAssertions().add(assertion);
    }

}
