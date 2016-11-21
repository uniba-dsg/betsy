package pebl.benchmark.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.HasExtensions;
import pebl.HasId;
import pebl.HasName;
import pebl.MapAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class Group implements HasId, HasName, HasExtensions {

    @XmlElement(required = true)
    private final String name;

    @XmlInverseReference(mappedBy="groups")
    private final Language language;

    @XmlElement(required = true)
    private final String description;

    @XmlElement(name = "featureSet")
    @XmlElementWrapper(name= "featureSets")
    private final List<FeatureSet> featureSets = new LinkedList<>();

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    @XmlElement(name="metric")
    @XmlElementWrapper(name="metrics")
    private final List<Metric> metrics = new LinkedList<>();

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions = new HashMap<>();

    public Group addMetric(MetricType metricType) {
        metrics.add(new Metric(metricType, getId()));

        return this;
    }

    public Group() {
        this("", new Language(), "");
    }

    public Group(String name, Language language, String description) {
        this.name = Objects.requireNonNull(name);
        this.language = Objects.requireNonNull(language);
        this.description = Objects.requireNonNull(description);

        this.id = String.join(HasId.SEPARATOR, language.getId(), name);

        this.language.addGroup(this);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Group " + getId();
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
        Group group = (Group) o;
        return Objects.equals(getId(), group.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    void addFeatureSet(FeatureSet featureSet) {
        if(featureSets.stream().anyMatch(fs -> fs.getId().equals(featureSet.getId()))) {
            return;
        }
        this.featureSets.add(featureSet);
    }

    public FeatureSet getOrCreate(String name) {
        Optional<FeatureSet> featureSetOptional = featureSets.stream().filter(fs -> fs.getName().equals(name)).findFirst();
        if(featureSetOptional.isPresent()) {
            return featureSetOptional.get();
        } else {
            return new FeatureSet(this, name);
        }
    }

    public List<FeatureSet> getFeatureSets() {
        return Collections.unmodifiableList(featureSets);
    }

    public Language getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    @Override
    public Map<String, String> getExtensions() {
        return extensions;
    }

    @Override
    public Group addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }
}
