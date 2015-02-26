package betsy.bpel.model;

import betsy.bpel.ws.TestPartnerPortTypeRegular;
import betsy.common.model.TestAssertion;
import betsy.common.model.TestCase;
import betsy.common.model.TestStep;
import betsy.bpel.model.steps.*;

public class BPELTestCase extends TestCase {

    public BPELTestCase buildPartnerConcurrencySetup() {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(TestPartnerPortTypeRegular.CODE_CONCURRENCY_DETECTION___RESET_COUNTERS));
        step.setTestPartner(true);

        return addStep(step);
    }

    public BPELTestCase assertConcurrencyAtPartner() {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(TestPartnerPortTypeRegular.CODE_CONCURRENCY_DETECTION___GET_TOTAL_CONCURRENT_ACCESS_AND_RESET));
        step.setTestPartner(true);
        step.setConcurrencyTest(true);

        return addStep(step);
    }

    public BPELTestCase assertNumberOfPartnerCalls(int value) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(TestPartnerPortTypeRegular.CODE_CONCURRENCY_DETECTION___GET_TOTAL_ACCESSES_AND_RESET));
        step.setTestPartner(true);
        step.setPartnerOutput(String.valueOf(value));

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
        step.setOperation(WsdlOperation.ASYNC);

        return addStep(step);
    }

    public BPELTestCase buildSyncOperationOutputAsLeast(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOutputAsLeast(String.valueOf(output));
        step.setOperation(WsdlOperation.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.SYNC);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, String output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setStringOperationOutput(output);
        step.setOperation(WsdlOperation.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.SYNC_STRING);

        return addStep(step);
    }

    public BPELTestCase sendSyncString(int input, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.SYNC_STRING);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOutput(String.valueOf(output));
        step.setOperation(WsdlOperation.SYNC);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase sendSync(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOutput(String.valueOf(output));
        step.setOperation(WsdlOperation.SYNC);

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
        step.setOperation(WsdlOperation.SYNC);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public BPELTestCase addStep(TestStep step) {
        getTestSteps().add(step);

        return this;
    }

}
