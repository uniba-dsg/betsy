package pebl.benchmark.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.HasId;
import pebl.HasName;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureDimension;
import pebl.benchmark.feature.Metric;
import pebl.benchmark.feature.MetricType;

@XmlAccessorType(XmlAccessType.NONE)
public class Test implements Comparable<Test>, HasName, HasId, FeatureDimension {

    @XmlIDREF
    @XmlElement(required = true)
    private final Feature feature;

    @XmlElement(required = true)
    private final Path process;

    @XmlElement(required = true)
    private final String description;

    @XmlElement(name="testCase")
    @XmlElementWrapper( name="testCases" )
    private final List<TestCase> testCases;

    @XmlElement(name="file")
    @XmlElementWrapper(name="files")
    private final List<Path> files;

    @XmlElement(required = true)
    @XmlElementRef
    @XmlElementWrapper( name="testPartners" )
    private final List<TestPartner> partners;

    @XmlElement
    private final Map<String, String> additionalData;

    @XmlElement(name = "metric")
    @XmlElementWrapper(name="metrics")
    private final List<Metric> metrics = new LinkedList<>();

    public Test addMetric(MetricType type) {
        metrics.add(new Metric(type, getId()));

        return this;
    }

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    public Test() {
        this(Paths.get(""), "", Collections.emptyList(), new Feature());
    }

    public Test(Path process,
            String description,
            List<? extends TestCase> testCases,
            Feature feature) {
        this(process, description, testCases, feature, Collections.emptyList(), Collections.emptyList());
    }

    public Test(Path process,
            String description,
            List<? extends TestCase> testCases,
            Feature feature,
            List<TestPartner> partners) {
        this(process, description, testCases, feature, Collections.emptyList(), partners);
    }

    public Test(Path process,
            String description,
            List<? extends TestCase> testCases,
            Feature feature,
            List<Path> files,
            List<TestPartner> partners) {
        this.partners = new ArrayList<>(Objects.requireNonNull(partners));
        this.process = Objects.requireNonNull(process);
        this.feature = Objects.requireNonNull(feature);
        this.description = Objects.requireNonNull(description);
        this.testCases = uniqueifyTestCaseNames(new ArrayList<>(Objects.requireNonNull(testCases)));
        this.files = new ArrayList<>(Objects.requireNonNull(files));
        this.additionalData = Collections.emptyMap();

        this.id = String.join(HasId.SEPARATOR,getFeature().getId(), "test");
    }

    public Test(Path process, List<TestCase> testCases,
            Feature feature,
            List<Path> files,
            String description,
            List<TestPartner> partners,
            Map<String, String> additionalData) {
        this.process = process;
        this.testCases = testCases;
        this.feature = feature;
        this.files = files;
        this.description = description;
        this.partners = partners;
        this.additionalData = additionalData;
        this.id = "";
    }

    public Test withNewProcessAndFeature(Path process, Feature feature) {
        return new Test(process, description, testCases, feature, files, partners);
    }

    public Test withNewTestCases(List<TestCase> testCases) {
        return new Test(process, description, testCases, feature, files, partners);
    }

    public List<Path> getFiles() {
        return Collections.unmodifiableList(files);
    }

    private List<TestCase> uniqueifyTestCaseNames(List<TestCase> testCases) {
        // make sure that the tests have different numbers
        for (int counter = 1; counter <= testCases.size(); counter++) {
            TestCase testCase = testCases.get(counter - 1);
            testCase.setNumber(counter);
        }

        // only when not already unique
        if (!hasUniqueTestCaseNames(testCases)) {
            return testCases;
        }

        // make test case names unique
        for (TestCase testCase : testCases) {
            testCase.setName(testCase.getName() + "-" + testCase.getNumber());
        }

        return testCases;
    }

    private boolean hasUniqueTestCaseNames(List<TestCase> testCases) {
        return testCases.stream().map(TestCase::getName).distinct().count() == testCases.size();
    }

    public String getProcessLanguage() {
        return getLanguage().getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!getClass().equals(o.getClass())) {
            return false;
        }

        Test that = (Test) o;

        return process.toString().equals(that.process.toString());

    }

    public int hashCode() {
        return process.hashCode();
    }

    @Override
    public int compareTo(Test o) {
        return process.compareTo(o.process);
    }

    public Path getProcess() {
        return this.process;
    }

    public String getDescription() {
        return description;
    }

    public List<TestCase> getTestCases() {
        return Collections.unmodifiableList(testCases);
    }

    private void setTestCases(List<TestCase> testCases) {
        uniqueifyTestCaseNames(testCases);
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    @Override
    public String getName() {
        return getFeature().getName();
    }

    public Feature getFeature() {
        return feature;
    }

    public List<Path> getFiles(Predicate<Path> predicate) {
        return getFiles().stream().filter(predicate).collect(Collectors.toList());
    }

    public List<TestPartner> getTestPartners() {
        return Collections.unmodifiableList(partners);
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    @Override
    public String getId() {
        return id;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }
}

