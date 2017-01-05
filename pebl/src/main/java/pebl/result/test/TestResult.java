package pebl.result.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import pebl.HasExtensions;
import pebl.HasId;
import pebl.MapAdapter;
import pebl.benchmark.test.Test;
import pebl.result.Measurement;
import pebl.result.engine.Engine;

@XmlAccessorType(XmlAccessType.NONE)
public class TestResult implements HasExtensions, HasId {

    @XmlAttribute(required = true)
    @XmlIDREF
    private final Test test;

    @XmlAttribute(required = true)
    @XmlIDREF
    private final Engine engine;

    @XmlAttribute(required = true)
    private final String tool;

    @XmlElement(name = "logs")
    @XmlList
    private final List<Path> logs;

    @XmlElement(required = true)
    private final Path deploymentPackage;

    @XmlElement(name = "files")
    @XmlList
    private final List<Path> files;

    @XmlElement(name = "measurement")
    @XmlElementWrapper(name = "measurements")
    private final List<Measurement> measurements;

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions;

    @XmlElement(name = "testCaseResult")
    @XmlElementWrapper(name = "testCaseResults")
    private final List<TestCaseResult> testCaseResults;

    TestResult() {
        this(new Test(), new Engine(), "",
                Collections.emptyList(),
                Paths.get(""),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyMap(), Collections.emptyList());
    }

    public TestResult(Test test,
            Engine engine,
            String tool,
            List<Path> logs,
            Path deploymentPackage,
            List<Path> files,
            List<Measurement> measurements,
            Map<String, String> extensions,
            List<TestCaseResult> testCaseResults) {
        this.test = test;
        this.engine = engine;
        this.tool = tool;
        this.logs = new ArrayList<>(logs);
        this.deploymentPackage = deploymentPackage;
        this.files = new ArrayList<>(files);
        this.measurements = new LinkedList<>(measurements);
        this.extensions = new HashMap<>(extensions);
        this.testCaseResults = new ArrayList<>(testCaseResults);
    }

    public Test getTest() {
        return test;
    }

    public Engine getEngine() {
        return engine;
    }

    public String getTool() {
        return tool;
    }

    public List<Path> getLogs() {
        return logs;
    }

    public Path getDeploymentPackage() {
        return deploymentPackage;
    }

    public List<Path> getFiles() {
        return files;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    @Override
    public TestResult addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }

    public List<TestCaseResult> getTestCaseResults() {
        return Collections.unmodifiableList(testCaseResults);
    }

    @Override
    public String getId() {
        return getEngine().getId() + HasId.SEPARATOR + getTest().getId();
    }
}
