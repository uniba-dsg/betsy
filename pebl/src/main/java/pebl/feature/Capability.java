package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import pebl.HasName;
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class Capability implements HasID, HasName {

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final List<Metric> atomicMetrics;

    @XmlElement(name="language")
    private final List<Language> languages = new LinkedList<>();

    @XmlElement
    private final List<ComputedMetric> computedMetrics = new LinkedList<>();

    public Capability addMetric(ValueType type, String name, String description, String unit, String groovyScript) {
        computedMetrics.add(new ComputedMetric(type, name, description, unit, getID(), groovyScript));

        return this;
    }

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    Capability() {
        this("", Collections.emptyList());
    }

    public Capability(String name, List<Metric> atomicMetrics) {
        this.name = Objects.requireNonNull(name);
        this.atomicMetrics = Objects.requireNonNull(new LinkedList<>(atomicMetrics));

        this.id = name;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Metric> getAtomicMetrics() {
        return atomicMetrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Capability construct = (Capability) o;
        return Objects.equals(getID(), construct.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    void addLanguage(Language language) {
        this.languages.add(language);
    }

    public List<Language> getLanguages() {
        return Collections.unmodifiableList(languages);
    }
}
