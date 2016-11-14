package pebl.benchmark.feature;

import java.util.Collections;
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.HasExtensions;
import pebl.HasName;
import pebl.HasId;
import pebl.MapAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class FeatureSet implements HasId, HasName, HasExtensions {

    @XmlInverseReference(mappedBy="featureSets")
    private final Group group;

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String description;

    @XmlElement(name = "feature")
    private final List<Feature> features = new LinkedList<>();

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    @XmlElement(name="metric")
    @XmlElementWrapper(name="metrics")
    private final List<Metric> metrics = new LinkedList<>();

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions = new HashMap<>();

    public FeatureSet addMetric(ScriptMetricType scriptMetricType) {
        metrics.add(new Metric(scriptMetricType, getId()));

        return this;
    }

    public FeatureSet() {
        this(new Group(), "");
    }

    public FeatureSet(Group group, String name) {
        this(group, name, "");
    }

    public FeatureSet(Group group, String name, String description) {
        this.group = Objects.requireNonNull(group);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);

        this.group.addFeatureSet(this);

        this.id = String.join(HasId.SEPARATOR, group.getId(), name);
    }

    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    void addFeature(Feature feature) {
        this.features.add(feature);
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
        FeatureSet featureSet = (FeatureSet) o;
        return Objects.equals(getId(), featureSet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Group getGroup() {
        return group;
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
    public FeatureSet addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }
}
