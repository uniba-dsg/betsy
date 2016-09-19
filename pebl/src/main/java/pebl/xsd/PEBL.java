package pebl.xsd;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.tool.Tool;

@XmlRootElement
public class PEBL {

    private Engines engines;

    private Tools tools;

    private Tests tests;

    private TestResults testResults;

    private Features features;

    public Engines getEngines() {
        return engines;
    }

    public void setEngines(Engines engines) {
        this.engines = engines;
    }

    public Tools getTools() {
        return tools;
    }

    public void setTools(Tools tools) {
        this.tools = tools;
    }

    public Tests getTests() {
        return tests;
    }

    public void setTests(Tests tests) {
        this.tests = tests;
    }

    public TestResults getTestResults() {
        return testResults;
    }

    public void setTestResults(TestResults testResults) {
        this.testResults = testResults;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }
}
