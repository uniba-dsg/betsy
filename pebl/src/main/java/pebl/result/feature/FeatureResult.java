package pebl.result.feature;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.benchmark.feature.Feature;
import pebl.result.Measurement;
import pebl.result.test.TestResult;

public class FeatureResult {

    @XmlIDREF
    @XmlElement(required = true)
    private final Feature feature;

    @XmlInverseReference(mappedBy = "featureSetResults")
    private final FeatureSetResult featureSetResult;

    @XmlElement
    private final List<Measurement> measurements = new LinkedList<>();

    @XmlElement
    private final List<TestResult> testResults = new LinkedList<>();

    public FeatureResult() {
        this(new Feature(), new FeatureSetResult());
    }

    public FeatureResult(Feature feature, FeatureSetResult featureSetResult) {
        this.feature = Objects.requireNonNull(feature);
        this.featureSetResult = Objects.requireNonNull(featureSetResult);
    }

    public Feature getFeature() {
        return feature;
    }

    public FeatureSetResult getFeatureSetResult() {
        return featureSetResult;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }
}
