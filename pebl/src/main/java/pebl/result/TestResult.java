package pebl.result;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.engine.Engine;
import pebl.test.Test;
import pebl.tool.Tool;

public class TestResult {

    private final Test test;
    private final Engine engine;
    private final Tool tool;

    private final long executionTimestamp;

    private final List<Path> logFiles;
    private final Path deploymentPackage;
    private final List<Path> files;

    private final long executionDuration;
    private final NamedMetrics metrics;
    private final Map<String, String> additionalInformation;
    private final List<TestCaseResult> testCaseResults;

    TestResult() {
        this(new Test(), new Engine(), new Tool(),
                Long.MIN_VALUE, Collections.emptyList(),
                Paths.get(""),
                Collections.emptyList(),Long.MIN_VALUE, new NamedMetrics(),
                Collections.emptyMap(), Collections.emptyList());
    }

    public TestResult(Test test,
            Engine engine,
            Tool tool,
            long executionTimestamp,
            List<Path> logFiles,
            Path deploymentPackage,
            List<Path> files,
            long executionDuration,
            NamedMetrics metrics,
            Map<String, String> additionalInformation,
            List<TestCaseResult> testCaseResults) {
        this.test = test;
        this.engine = engine;
        this.tool = tool;
        this.executionTimestamp = executionTimestamp;
        this.logFiles = logFiles;
        this.deploymentPackage = deploymentPackage;
        this.files = files;
        this.executionDuration = executionDuration;
        this.metrics = metrics;
        this.additionalInformation = additionalInformation;
        this.testCaseResults = testCaseResults;
    }

    @XmlElement(required = true)
    @XmlIDREF
    public Test getTest() {
        return test;
    }

    @XmlElement(required = true)
    @XmlIDREF
    public Engine getEngine() {
        return engine;
    }

    @XmlElement(required = true)
    @XmlIDREF
    public Tool getTool() {
        return tool;
    }

    @XmlElement(required = true)
    public long getExecutionTimestamp() {
        return executionTimestamp;
    }

    @XmlElement
    public List<Path> getLogFiles() {
        return logFiles;
    }

    @XmlElement(required = true)
    public Path getDeploymentPackage() {
        return deploymentPackage;
    }

    @XmlElement
    public List<Path> getFiles() {
        return files;
    }

    @XmlElement(required = true)
    public long getExecutionDuration() {
        return executionDuration;
    }

    @XmlElement(required = true)
    public NamedMetrics getMetrics() {
        return metrics;
    }

    @XmlElement
    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    @XmlElement
    public List<TestCaseResult> getTestCaseResults() {
        return testCaseResults;
    }
}
