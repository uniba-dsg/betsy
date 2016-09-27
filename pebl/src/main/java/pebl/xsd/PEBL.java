package pebl.xsd;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.result.engine.Engine;
import pebl.benchmark.feature.Capability;
import pebl.result.test.TestResult;
import pebl.result.feature.CapabilityResult;
import pebl.benchmark.test.Test;
import pebl.result.tool.Tool;

@XmlRootElement
public class PEBL {

    public static class Benchmark {

        @XmlElement(name="capability")
        public List<Capability> capabilities = new LinkedList<>();

        @XmlElement(name = "test")
        public List<Test> tests = new LinkedList<>();

    }

    public static class Result {

        @XmlElement(name="engine")
        public List<Engine> engines = new LinkedList<>();

        @XmlElement(name = "tool")
        public List<Tool> tools = new LinkedList<>();

        @XmlElement(name = "testResult")
        public List<TestResult> testResults = new LinkedList<>();

        @XmlElement(name = "capabilityResult")
        public List<CapabilityResult> capabilityResults = new LinkedList<>();

    }

    @XmlElement
    public Benchmark benchmark = new Benchmark();

    @XmlElement
    public Result result = new Result();

}
