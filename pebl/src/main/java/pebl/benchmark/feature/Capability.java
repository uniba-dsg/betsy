package pebl.benchmark.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;

import pebl.HasName;
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class Capability implements HasID, HasName {

    @XmlElement(required = true)
    private final String name;

    @XmlElement(name="language")
    private final List<Language> languages = new LinkedList<>();

    @XmlElement
    private final List<AggregatedMetric> derivedMetrics = new LinkedList<>();

    @XmlElement
    private final List<Characteristic> characteristics = new LinkedList<>();

    public Capability addMetric(ValueType type, String name, String description, String unit, String groovyScript) {
        derivedMetrics.add(new AggregatedMetric(type, name, description, unit, getID(), groovyScript));

        return this;
    }

    public Capability addCharacteristic(Characteristic c) {
        characteristics.add(c);

        return this;
    }

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    public Capability() {
        this("");
    }

    public Capability(String name) {
        this.name = Objects.requireNonNull(name);

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
