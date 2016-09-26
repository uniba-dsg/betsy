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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import pebl.HasName;
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class FeatureSet implements HasID, HasName {

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

    @XmlElement
    private final List<ComputedMetric> computedMetrics = new LinkedList<>();

    public FeatureSet addMetric(ValueType type, String name, String description, String unit, String groovyScript) {
        computedMetrics.add(new ComputedMetric(type, name, description, unit, getID(), groovyScript));

        return this;
    }

    FeatureSet() {
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

        this.id = String.join(HasID.SEPARATOR, group.getID(), name);
    }

    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    void addFeature(Feature feature) {
        this.features.add(feature);
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
        FeatureSet featureSet = (FeatureSet) o;
        return Objects.equals(getID(), featureSet.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    public Group getGroup() {
        return group;
    }

    public String getDescription() {
        return description;
    }
}
