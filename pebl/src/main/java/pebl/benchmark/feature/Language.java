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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.HasExtensions;
import pebl.HasId;
import pebl.HasName;
import pebl.MapAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class Language implements HasId, HasName, HasExtensions, HasMetrics {

    @XmlInverseReference(mappedBy="languages")
    private final Capability capability;

    @XmlElement(required = true)
    private final String name;

    @XmlElement(name="group")
    @XmlElementWrapper(name= "groups")
    private final List<Group> groups = new LinkedList<>();

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    @XmlElement(name="metric")
    @XmlElementWrapper(name="metrics")
    private final List<Metric> metrics = new LinkedList<>();

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions = new HashMap<>();

    public Language addMetric(MetricType metricType) {
        metrics.add(new Metric(metricType, getId()));

        return this;
    }

    public Language() {
        this(new Capability(), "");
    }

    public Language(Capability capability, String name) {
        this.capability = Objects.requireNonNull(capability);
        this.name = Objects.requireNonNull(name);

        this.id = String.join(HasId.SEPARATOR, capability.getId(), name);

        this.capability.addLanguage(this);
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
        Language construct = (Language) o;
        return Objects.equals(getId(), construct.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    void addGroup(Group group) {
        this.groups.add(group);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Capability getCapability() {
        return capability;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    @Override
    public Map<String, String> getExtensions() {
        return extensions;
    }

    @Override
    public Language addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }

    @Override
    public String toString() {
        return getId();
    }
}
