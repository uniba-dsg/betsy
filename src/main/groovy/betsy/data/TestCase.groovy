package betsy.data

class TestCase {

    /**
     * List of test steps.
     */
    List<TestStep> testSteps

    /**
     * The name of the test case.
     */
    String name = "Good-Case"

    boolean isNotDeployable() {
        testSteps.any {it.notDeployable }
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "testSteps=" + testSteps +
                ", name='" + name + '\'' +
                '}';
    }
}
