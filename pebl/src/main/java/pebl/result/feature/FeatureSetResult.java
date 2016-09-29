package pebl.result.feature;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.benchmark.feature.FeatureSet;
import pebl.result.Measurement;

public class FeatureSetResult {

    @XmlIDREF
    @XmlElement(required = true)
    private final FeatureSet featureSet;

    @XmlInverseReference(mappedBy = "featureSetResults")
    private final GroupResult groupResult;

    @XmlElement
    private final List<Measurement> measurements = new LinkedList<>();

    @XmlElement
    private final List<FeatureResult> featureResults = new LinkedList<>();

    public FeatureSetResult() {
        this(new FeatureSet(), new GroupResult());
    }

    public FeatureSetResult(FeatureSet featureSet, GroupResult groupResult) {
        this.featureSet = Objects.requireNonNull(featureSet);
        this.groupResult = Objects.requireNonNull(groupResult);
    }

    public FeatureSet getFeatureSet() {
        return featureSet;
    }

    public GroupResult getGroupResult() {
        return groupResult;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public List<FeatureResult> getFeatureResults() {
        return featureResults;
    }
}
