package pebl.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.test.steps.DelayTestStep;
import pebl.test.steps.DeployableCheckTestStep;
import pebl.test.steps.GatherTracesTestStep;
import pebl.test.steps.NotDeployableCheckTestStep;
import pebl.test.steps.soap.SoapTestStep;
import pebl.test.steps.vars.ProcessStartWithVariablesTestStep;

@XmlSeeAlso({SoapTestStep.class, ProcessStartWithVariablesTestStep.class, DelayTestStep.class,
        DeployableCheckTestStep.class, NotDeployableCheckTestStep.class, GatherTracesTestStep.class})
public class TestStep {

    /**
     * just for documentation purposes
     */
    private String description;

    /**
     * List of assertions which are evaluated after the test step has been executed/the messages have been sent.
     */
    private List<TestAssertion> assertions = new ArrayList<>();

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public List<TestAssertion> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<TestAssertion> assertions) {
        this.assertions = assertions;
    }

    public TestStep addAssertion(TestAssertion testAssertion) {
        this.assertions.add(testAssertion);

        return this;
    }

    @Override
    public String toString() {
        return "TestStep{" +
                "description='" + description + '\'' +
                '}';
    }

}
