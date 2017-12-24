package pebl.benchmark.feature;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import pebl.HasExtensions;
import pebl.HasId;
import pebl.HasName;
import pebl.MapAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class Capability implements HasId, HasName, HasExtensions, HasMetrics {

    @XmlElement(required = true)
    private final String name;

    @XmlElement(name="language")
    @XmlElementWrapper(name= "languages")
    private final List<Language> languages = new LinkedList<>();

    @XmlElement(name="metric")
    @XmlElementWrapper(name="metrics")
    private final List<Metric> metrics = new LinkedList<>();

    @XmlElement(name="characteristics")
    @XmlList
    private final List<Characteristic> characteristics = new LinkedList<>();

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions = new HashMap<>();

    public Capability addMetric(MetricType metricType) {
        metrics.add(new Metric(metricType, getId()));

        return this;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
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
    public String getId() {
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
        return Objects.equals(getId(), construct.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    void addLanguage(Language language) {
        this.languages.add(language);
    }

    public List<Language> getLanguages() {
        return languages;
    }

    @Override
    public Map<String, String> getExtensions() {
        return extensions;
    }

    @Override
    public Capability addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }

    @Override
    public String toString() {
        return getId();
    }
}
