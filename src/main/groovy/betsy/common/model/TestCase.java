package betsy.common.model;

import betsy.common.model.steps.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TestCase implements the builder pattern using a fluent interface.
 */
public class TestCase {
    public TestCase addStep(TestStep step) {
        testSteps.add(step);

        return this;
    }

    public TestCase buildPartnerConcurrencySetup() {
        SoapTestStep step = new SoapTestStep();
        step.setInput("102");
        step.setTestPartner(true);

        return addStep(step);
    }

    public TestCase buildPartnerConcurrencyCheck() {
        SoapTestStep step = new SoapTestStep();
        step.setInput("101");
        step.setTestPartner(true);
        step.setConcurrencyTest(true);

        return addStep(step);
    }

    public TestCase buildPartnerValueCheck(int value) {
        SoapTestStep step = new SoapTestStep();
        step.setInput("102");
        step.setTestPartner(true);
        step.setPartnerOutput(String.valueOf(value));

        return addStep(step);
    }

    public TestCase checkDeployment() {
        return addStep(new DeployableCheckTestStep());
    }

    public TestCase checkFailedDeployment() {
        return addStep(new NotDeployableCheckTestStep());
    }

    public TestCase sendAsync(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.ASYNC);

        return addStep(step);
    }

    public TestCase buildSyncOperationOutputAsLeast(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOutputAsLeast(String.valueOf(output));
        step.setOperation(WsdlOperation.SYNC);

        return addStep(step);
    }

    public TestCase sendSync(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.SYNC);

        return addStep(step);
    }

    public TestCase sendSyncString(int input, String output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setStringOperationOutput(output);
        step.setOperation(WsdlOperation.SYNC_STRING);

        return addStep(step);
    }

    public TestCase sendSyncString(int input) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.SYNC_STRING);

        return addStep(step);
    }

    public TestCase sendSyncString(int input, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.SYNC_STRING);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public TestCase sendSync(int input, int output, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOutput(String.valueOf(output));
        step.setOperation(WsdlOperation.SYNC);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    public TestCase sendSync(int input, int output) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOutput(String.valueOf(output));
        step.setOperation(WsdlOperation.SYNC);

        return addStep(step);
    }

    public TestCase waitFor(int timeout) {
        DelayTestStep step = new DelayTestStep();
        step.setTimeToWaitAfterwards(timeout);

        return addStep(step);
    }

    public TestCase sendSync(int input, TestAssertion assertion) {
        SoapTestStep step = new SoapTestStep();
        step.setInput(String.valueOf(input));
        step.setOperation(WsdlOperation.SYNC);
        step.getAssertions().add(assertion);

        return addStep(step);
    }

    @Override
    public String toString() {
        return "TestCase{" + "testSteps=" + testSteps + ", name='" + name + "\'" + "}";
    }

    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(List<TestStep> testSteps) {
        this.testSteps = testSteps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * List of test steps.
     */
    private List<TestStep> testSteps = new ArrayList<>();
    /**
     * The name of the test case.
     */
    private String name = "Good-Case";
}
