package pebl.benchmark.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;

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
    @XmlElementRef
    @XmlElementWrapper( name="testSteps" )
    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    @XmlAttribute(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(required = true)
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
