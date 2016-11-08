package pebl.benchmark.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.benchmark.test.steps.DelayTesting;
import pebl.benchmark.test.steps.CheckDeployment;
import pebl.benchmark.test.steps.GatherTraces;
import pebl.benchmark.test.steps.CheckUndeployment;
import pebl.benchmark.test.steps.soap.SendSoapMessage;
import pebl.benchmark.test.steps.vars.StartProcess;

@XmlSeeAlso({SendSoapMessage.class, StartProcess.class, DelayTesting.class,
        CheckDeployment.class, CheckUndeployment.class, GatherTraces.class})
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
