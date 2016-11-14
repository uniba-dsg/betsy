package pebl.benchmark.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import pebl.HasExtensions;
import pebl.MapAdapter;
import pebl.benchmark.test.steps.CheckDeployment;
import pebl.benchmark.test.steps.DelayTesting;
import pebl.benchmark.test.steps.ExecuteScript;
import pebl.benchmark.test.steps.GatherTraces;
import pebl.benchmark.test.steps.soap.SendSoapMessage;
import pebl.benchmark.test.steps.vars.StartProcess;

@XmlSeeAlso({SendSoapMessage.class, StartProcess.class, DelayTesting.class,
        CheckDeployment.class, GatherTraces.class, ExecuteScript.class})
public class TestStep implements HasExtensions {

    /**
     * just for documentation purposes
     */
    private String description;

    /**
     * List of assertions which are evaluated after the test step has been executed/the messages have been sent.
     */
    private List<TestAssertion> assertions = new ArrayList<>();

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions = new HashMap<>();

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public TestStep addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    @XmlElementRef
    @XmlElementWrapper( name="testAssertions" )
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
