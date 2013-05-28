package betsy.data

/**
 * TestCase implements the builder pattern using a fluent interface.
 */
class TestCase {

    /**
     * List of test steps.
     */
    List<TestStep> testSteps = []

    /**
     * The name of the test case.
     */
    String name = "Good-Case"

    public TestCase addStep(TestStep step) {
        testSteps.add(step)

        this
    }

    public TestCase buildPartnerConcurrencySetup() {
        addStep(new SoapTestStep(input: "102", testPartner: true))
    }

    public TestCase buildPartnerConcurrencyCheck() {
        addStep(new SoapTestStep(input: "101", testPartner: true, concurrencyTest: true))
    }

    public TestCase buildPartnerValueCheck(int value) {
        addStep(new SoapTestStep(input: "102", testPartner: true, partnerOutput: "$value"))
    }

    public TestCase checkDeployment() {
        addStep(new DeployableCheckTestStep())
    }

    public TestCase checkFailedDeployment() {
        addStep(new NotDeployableCheckTestStep())
    }

    public TestCase sendAsync(int input) {
        addStep(new SoapTestStep(input: "$input", operation: WsdlOperation.ASYNC))
    }

    public TestCase buildSyncOperationOutputAsLeast(int input, int output) {
        addStep(new SoapTestStep(input: "$input", outputAsLeast: "$output", operation: WsdlOperation.SYNC))
    }

    public TestCase sendSync(int input) {
        addStep(new SoapTestStep(input: "$input", operation: WsdlOperation.SYNC))
    }

    public TestCase sendSyncString(int input, String output) {
        addStep(new SoapTestStep(input: "$input", stringOperationOutput: output, operation: WsdlOperation.SYNC_STRING))
    }

    public TestCase sendSyncString(int input) {
        addStep(new SoapTestStep(input: "$input", operation: WsdlOperation.SYNC_STRING))
    }

    public TestCase sendSyncString(int input, TestAssertion assertion) {
        TestStep step = new SoapTestStep(input: "$input", operation: WsdlOperation.SYNC_STRING)
        step.assertions.add(assertion)
        addStep(step)
    }

    public TestCase sendSync(int input, int output, TestAssertion assertion) {
        TestStep step = new SoapTestStep(input: "$input", output: "$output", operation: WsdlOperation.SYNC)
        step.assertions.add(assertion)
        addStep(step)
    }

    public TestCase sendSync(int input, int output) {
        addStep(new SoapTestStep(input: "$input", output: "$output", operation: WsdlOperation.SYNC))
    }

    public TestCase waitFor(int timeout) {
        addStep(new DelayTestStep(timeToWaitAfterwards: timeout))
    }

    public TestCase sendSync(int input, TestAssertion assertion) {
        TestStep step = new SoapTestStep(input: "$input", assertions: [assertion], operation: WsdlOperation.SYNC)
        step.assertions.add(assertion)
        addStep(step)
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "testSteps=" + testSteps +
                ", name='" + name + '\'' +
                '}';
    }
}
