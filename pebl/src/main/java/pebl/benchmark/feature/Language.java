package pebl.benchmark.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.HasName;
import pebl.HasId;

@XmlAccessorType(XmlAccessType.NONE)
public class Language implements HasId, HasName {

    @XmlInverseReference(mappedBy="languages")
    private final Capability capability;

    @XmlElement(required = true)
    private final String name;

    @XmlElement(name="group")
    private final List<Group> groups = new LinkedList<>();

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    @XmlElement
    private final List<Metric> metrics = new LinkedList<>();

    public Language addMetric(ScriptMetricType scriptMetricType) {
        metrics.add(new Metric(scriptMetricType, getId()));

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
        return Collections.unmodifiableList(groups);
    }

    public Capability getCapability() {
        return capability;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }
}
