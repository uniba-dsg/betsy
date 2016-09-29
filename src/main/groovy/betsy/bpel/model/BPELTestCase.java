package betsy.bpel.model;

import java.util.Objects;

import pebl.benchmark.test.TestAssertion;
import pebl.benchmark.test.TestCase;
import pebl.benchmark.test.TestStep;
import pebl.benchmark.test.assertions.XpathTestAssertion;
import pebl.benchmark.test.steps.DelayTestStep;
import pebl.benchmark.test.steps.DeployableCheckTestStep;
import pebl.benchmark.test.steps.NotDeployableCheckTestStep;
import pebl.benchmark.test.steps.soap.SoapTestStep;
import pebl.benchmark.test.steps.soap.WsdlService;

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
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase assertConcurrencyAtPartner() {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_CONCURRENT_ACCESS));
        step.setService(new WsdlService("testPartner"));
        step.setOperation(BPELWsdlOperations.SYNC);

        XpathTestAssertion assertion = new XpathTestAssertion(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncResponse > 0",
                "true"
        );
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase assertNumberOfPartnerCalls(int value) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_ACCESSES));
        step.setService(new WsdlService("testPartner"));
        setPartnerOutput(step, String.valueOf(value));
        step.setOperation(BPELWsdlOperations.SYNC);

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
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.ASYNC);

        return addStep(step);
    }

    public BPELTestCase buildSyncOperationOutputAsLeast(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setService(new WsdlService("testInterface"));
        setOutputAsLeast(step, String.valueOf(output));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, String output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        setStringOperationOutput(step, output);
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setService(new WsdlService("testInterface"));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(BPELWsdlOperations.SYNC_STRING);
        step.setService(new WsdlService("testInterface"));
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        setOutput(step, String.valueOf(output));
        step.setOperation(BPELWsdlOperations.SYNC);
        step.setService(new WsdlService("testInterface"));
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        setOutput(step, String.valueOf(output));
        step.setService(new WsdlService("testInterface"));
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
        step.setService(new WsdlService("testInterface"));

        return addStep(step);
    }

    public BPELTestCase addStep(TestStep step) {
        getTestSteps().add(step);

        return this;
    }

    public void setOutput(SoapTestStep step, String output) {
        XpathTestAssertion assertion = new XpathTestAssertion(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';number(//test:testElementSyncResponse) cast as xs:integer",
                output
        );
        step.getAssertions().add(assertion);
    }

    public void setPartnerOutput(SoapTestStep step, String output) {
        XpathTestAssertion assertion = new XpathTestAssertion(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';number(//test:testElementSyncResponse) cast as xs:integer",
                output
        );
        step.getAssertions().add(assertion);
    }

    public void setStringOperationOutput(SoapTestStep step, String output) {
        XpathTestAssertion assertion = new XpathTestAssertion(
                "declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface';//test:testElementSyncStringResponse",
                output
        );
        step.getAssertions().add(assertion);
    }

    public void setOutputAsLeast(SoapTestStep step, final String output) {
        XpathTestAssertion assertion = new XpathTestAssertion(
                "declare namespace test=\'http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface\';//test:testElementSyncResponse >= ",
                "true"
        );
        step.getAssertions().add(assertion);
    }

}
