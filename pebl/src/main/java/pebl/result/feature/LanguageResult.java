package pebl.result.feature;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.benchmark.feature.Language;
import pebl.result.Measurement;

public class LanguageResult {

    @XmlIDREF
    @XmlElement(required = true)
    private final Language language;

    @XmlInverseReference(mappedBy = "languageResults")
    private final CapabilityResult capabilityResult;

    @XmlElement
    private final List<Measurement> measurements = new LinkedList<>();

    @XmlElement
    private final List<GroupResult> groupResults = new LinkedList<>();

    public LanguageResult() {
        this(new Language(), new CapabilityResult());
    }

    public LanguageResult(Language language, CapabilityResult capabilityResult) {
        this.language = Objects.requireNonNull(language);
        this.capabilityResult = Objects.requireNonNull(capabilityResult);
    }

    public Language getLanguage() {
        return language;
    }

    public CapabilityResult getCapabilityResult() {
        return capabilityResult;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public List<GroupResult> getGroupResults() {
        return groupResults;
    }
}
