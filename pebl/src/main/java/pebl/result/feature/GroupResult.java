package pebl.result.feature;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.benchmark.feature.Group;
import pebl.result.Measurement;

public class GroupResult {

    @XmlIDREF
    @XmlElement(required = true)
    private final Group group;

    @XmlInverseReference(mappedBy = "groupResults")
    private final LanguageResult languageResult;

    @XmlElement
    private final List<Measurement> measurements = new LinkedList<>();

    @XmlElement
    private final List<FeatureSetResult> featureSetResults = new LinkedList<>();

    public GroupResult() {
        this(new Group(), new LanguageResult());
    }

    public GroupResult(Group group, LanguageResult languageResult) {
        this.group = Objects.requireNonNull(group);
        this.languageResult = Objects.requireNonNull(languageResult);
    }

    public Group getGroup() {
        return group;
    }

    public LanguageResult getLanguageResult() {
        return languageResult;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public List<FeatureSetResult> getFeatureSetResults() {
        return featureSetResults;
    }
}
