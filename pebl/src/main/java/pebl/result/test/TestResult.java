package pebl.result.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.result.engine.Engine;
import pebl.result.Measurement;
import pebl.benchmark.test.Test;
import pebl.result.tool.Tool;

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

    @XmlElement(name="logFile")
    @XmlElementWrapper(name="logFiles")
    private final List<Path> logFiles;

    @XmlElement(required = true)
    private final Path deploymentPackage;

    @XmlElement(name="file")
    @XmlElementWrapper(name="files")
    private final List<Path> files;

    @XmlElement(name="measurement")
    @XmlElementWrapper(name="measurements")
    private final List<Measurement> measurements;

    @XmlElement
    private final Map<String, String> extension;

    @XmlElement
    private final List<TestCaseResult> testCaseResults;

    TestResult() {
        this(new Test(), new Engine(), new Tool(),
                Collections.emptyList(),
                Paths.get(""),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyMap(), Collections.emptyList());
    }

    public TestResult(Test test,
            Engine engine,
            Tool tool,
            List<Path> logFiles,
            Path deploymentPackage,
            List<Path> files,
            List<Measurement> measurements,
            Map<String, String> extension,
            List<TestCaseResult> testCaseResults) {
        this.test = test;
        this.engine = engine;
        this.tool = tool;
        this.logFiles = new ArrayList<>(logFiles);
        this.deploymentPackage = deploymentPackage;
        this.files = new ArrayList<>(files);
        this.measurements = measurements;
        this.extension = extension;
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

    public List<Path> getLogFiles() {
        return Collections.unmodifiableList(logFiles);
    }

    public Path getDeploymentPackage() {
        return deploymentPackage;
    }

    public List<Path> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public List<TestCaseResult> getTestCaseResults() {
        return Collections.unmodifiableList(testCaseResults);
    }
}
