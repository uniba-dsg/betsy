package pebl.result.feature;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.benchmark.feature.Capability;
import pebl.result.Measurement;

public class CapabilityResult {

    @XmlElement(required = true)
    @XmlIDREF
    private final Capability capability;

    @XmlElement
    private final List<Measurement> measurements = new LinkedList<>();

    @XmlElement
    private final List<LanguageResult> languages = new LinkedList<>();

    public CapabilityResult() {
        this(new Capability());
    }

    public CapabilityResult(Capability capability) {
        this.capability = Objects.requireNonNull(capability);
    }

    public Capability getCapability() {
        return capability;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public List<LanguageResult> getLanguages() {
        return languages;
    }
}
