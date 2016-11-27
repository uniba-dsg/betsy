package betsy.bpel.model;

import java.util.Objects;

import pebl.benchmark.test.TestAssertion;
import pebl.benchmark.test.TestCase;
import pebl.benchmark.test.TestStep;
import pebl.benchmark.test.assertions.AssertDeployed;
import pebl.benchmark.test.assertions.AssertNotDeployed;
import pebl.benchmark.test.assertions.AssertXpath;
import pebl.benchmark.test.steps.DelayTesting;
import pebl.benchmark.test.steps.CheckDeployment;
import pebl.benchmark.test.steps.soap.SendSoapMessage;
import pebl.benchmark.test.steps.soap.WsdlService;

public class BPELTestCase extends TestCase {

    public BPELTestCase() {

    }

    public BPELTestCase(String name) {
        this.setName(Objects.requireNonNull(name));
    }

    public BPELTestCase buildPartnerConcurrencySetup() {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___RESET_COUNTERS));
        step.setService(new WsdlService("testPartner"));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public static String getRequestContent(SendSoapMessage testStep) {
        String requestContent;
        if (BPELWsdlOperations.SYNC.equals(testStep.getOperation())) {
            requestContent = TestMessages.createSyncInputMessage(testStep.getSoapMessage());
        } else if (BPELWsdlOperations.ASYNC.equals(testStep.getOperation())) {
            requestContent = TestMessages.createAsyncInputMessage(testStep.getSoapMessage());
        } else if (testStep.getService().equals(new WsdlService("testPartner"))) {
            requestContent = TestMessages.createSyncTestPartnerInputMessage(testStep.getSoapMessage());
        } else {
            requestContent = TestMessages.createSyncStringInputMessage(testStep.getSoapMessage());
        }
        return requestContent;
    }

    public BPELTestCase assertConcurrencyAtPartner() {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_CONCURRENT_ACCESS));
        step.setService(new WsdlService("testPartner"));
        step.setOperation(BPELWsdlOperations.SYNC);

        AssertXpath assertion = new AssertXpath(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncResponse > 0",
                "true"
        );
        step.getTestAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase assertNumberOfPartnerCalls(int value) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_ACCESSES));
        step.setService(new WsdlService("testPartner"));
        setPartnerOutput(step, String.valueOf(value));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase checkDeployment() {
        return addStep(new CheckDeployment().addAssertion(new AssertDeployed()));
    }

    public BPELTestCase checkFailedDeployment() {
        return addStep(new CheckDeployment().addAssertion(new AssertNotDeployed()));
    }

    public BPELTestCase sendAsync(int input) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.ASYNC);

        return addStep(step);
    }

    public BPELTestCase buildSyncOperationOutputAsLeast(int input, int output) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        step.setService(new WsdlService("testInterface"));
        setOutputAsLeast(step, String.valueOf(output));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, String output) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        setStringOperationOutput(step, output);
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, TestAssertion assertion) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);
        step.setService(new WsdlService("testInterface"));
        step.getTestAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output, TestAssertion assertion) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        setOutput(step, String.valueOf(output));
        step.setOperation(BPELWsdlOperations.SYNC);
        step.setService(new WsdlService("testInterface"));
        step.getTestAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        setOutput(step, String.valueOf(output));
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase waitFor(int timeout) {
        DelayTesting step = new DelayTesting();
        step.setMilliseconds(timeout);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, TestAssertion assertion) {
        SendSoapMessage step = new SendSoapMessage();
        step.setSoapMessage(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.SYNC);
        step.getTestAssertions().add(assertion);
        step.setService(new WsdlService("testInterface"));

        return addStep(step);
    }

    public BPELTestCase addStep(TestStep step) {
        if(step instanceof SendSoapMessage) {
            final SendSoapMessage sendSoapMessage = (SendSoapMessage) step;
            sendSoapMessage.setSoapMessage(getRequestContent(sendSoapMessage));
        }

        getTestSteps().add(step);

        return this;
    }

    public void setOutput(SendSoapMessage step, String output) {
        AssertXpath assertion = new AssertXpath(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';number(//test:testElementSyncResponse) cast as xs:integer",
                output
        );
        step.getTestAssertions().add(assertion);
    }

    public void setPartnerOutput(SendSoapMessage step, String output) {
        AssertXpath assertion = new AssertXpath(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';number(//test:testElementSyncResponse) cast as xs:integer",
                output
        );
        step.getTestAssertions().add(assertion);
    }

    public void setStringOperationOutput(SendSoapMessage step, String output) {
        AssertXpath assertion = new AssertXpath(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';//test:testElementSyncStringResponse",
                output
        );
        step.getTestAssertions().add(assertion);
    }

    public void setOutputAsLeast(SendSoapMessage step, final String output) {
        AssertXpath assertion = new AssertXpath(
                "declare namespace test=\'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\';//test:testElementSyncResponse >= ",
                "true"
        );
        step.getTestAssertions().add(assertion);
    }

}
