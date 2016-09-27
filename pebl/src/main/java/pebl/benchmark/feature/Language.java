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
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class Language implements HasID, HasName {

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
    private final List<AggregatedMetric> derivedMetrics = new LinkedList<>();

    public Language addMetric(ValueType type, String name, String description, String unit, String groovyScript) {
        derivedMetrics.add(new AggregatedMetric(type, name, description, unit, getID(), groovyScript));

        return this;
    }

    public Language() {
        this(new Capability(), "");
    }

    public Language(Capability capability, String name) {
        this.capability = Objects.requireNonNull(capability);
        this.name = Objects.requireNonNull(name);

        this.id = String.join(HasID.SEPARATOR, capability.getID(), name);

        this.capability.addLanguage(this);
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
        Language construct = (Language) o;
        return Objects.equals(getID(), construct.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
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

}
