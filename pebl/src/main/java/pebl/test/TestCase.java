package pebl.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * TestCase implements the builder pattern using a fluent interface.
 */
public class TestCase {
    /**
     * List of test steps.
     */
    private List<TestStep> testSteps = new ArrayList<>();
    /**
     * The name of the test case.
     */
    private String name = "Good-Case";
    private int number = 1;

    public TestCase addStep(TestStep step) {
        testSteps.add(step);

        return this;
    }

    @XmlElement
    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(required = true)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "TestCase{" + "testSteps=" + testSteps + ", name='" + name + "\'" + "}";
    }
}
