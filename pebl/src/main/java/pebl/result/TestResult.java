package pebl.result;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.engine.Engine;
import pebl.test.Test;
import pebl.tool.Tool;

@XmlAccessorType(XmlAccessType.NONE)
public class TestResult {

    @XmlElement(required = true)
    @XmlIDREF
    private final Test test;

    @XmlElement(required = true)
    @XmlIDREF
    private final Engine engine;

    @XmlElement(required = true)
    @XmlIDREF
    private final Tool tool;

    @XmlElement(required = true)
    private final long executionTimestamp;

    @XmlElement
    private final List<Path> logFiles;

    @XmlElement(required = true)
    private final Path deploymentPackage;

    @XmlElement
    private final List<Path> files;

    @XmlElement(required = true)
    private final long executionDuration;

    @XmlElement(required = true)
    private final NamedMetrics metrics;

    @XmlElement
    private final Map<String, String> additionalInformation;

    @XmlElement
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
        this.logFiles = new ArrayList<>(logFiles);
        this.deploymentPackage = deploymentPackage;
        this.files = new ArrayList<>(files);
        this.executionDuration = executionDuration;
        this.metrics = metrics;
        this.additionalInformation = additionalInformation;
        this.testCaseResults = new ArrayList<>(testCaseResults);
    }

    public Test getTest() {
        return test;
    }

    public Engine getEngine() {
        return engine;
    }

    public Tool getTool() {
        return tool;
    }

    public long getExecutionTimestamp() {
        return executionTimestamp;
    }

    public List<Path> getLogFiles() {
        return Collections.unmodifiableList(logFiles);
    }

    public Path getDeploymentPackage() {
        return deploymentPackage;
    }

    public List<Path> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public long getExecutionDuration() {
        return executionDuration;
    }

    public NamedMetrics getMetrics() {
        return metrics;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    public List<TestCaseResult> getTestCaseResults() {
        return Collections.unmodifiableList(testCaseResults);
    }
}
