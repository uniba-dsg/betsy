package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.result.engine.Engine;
import pebl.benchmark.feature.Capability;
import pebl.result.test.TestResult;
import pebl.result.feature.FeatureResult;
import pebl.benchmark.test.Test;
import pebl.result.tool.Tool;

@XmlRootElement
public class PEBL {

    public static class Benchmark {

        @XmlElement(name="capability")
        @XmlElementWrapper(name="capabilities")
        public List<Capability> capabilities = new LinkedList<>();

        @XmlElement(name = "test")
        @XmlElementWrapper(name="tests")
        public List<Test> tests = new LinkedList<>();

    }

    public static class Result {

        @XmlElement(name="engine")
        @XmlElementWrapper(name="engines")
        public List<Engine> engines = new LinkedList<>();

        @XmlElement(name = "tool")
        @XmlElementWrapper(name="tools")
        public List<Tool> tools = new LinkedList<>();

        @XmlElement(name = "testResult")
        @XmlElementWrapper(name="testResults")
        public List<TestResult> testResults = new LinkedList<>();

        @XmlElement(name = "featureResult")
        @XmlElementWrapper(name="featureResults")
        public List<FeatureResult> featureResults = new LinkedList<>();

    }

    @XmlElement(required = true)
    public Benchmark benchmark = new Benchmark();

    @XmlElement(required = true)
    public Result result = new Result();

}
