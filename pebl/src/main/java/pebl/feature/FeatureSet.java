package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.HasName;
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class FeatureSet implements HasID, HasName {

    private final Group group;
    private final String name;
    private final String description;
    private final List<Feature> features = new LinkedList<>();

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
    }

    @XmlIDREF
    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    void addFeature(Feature feature) {
        this.features.add(feature);
    }

    @Override
    @XmlID
    @XmlElement(required = true)
    public String getID() {
        return String.join(HasID.SEPARATOR, group.getID(), name);
    }

    @Override
    @XmlElement(required = true)
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

    @XmlIDREF
    @XmlElement(required = true)
    public Group getGroup() {
        return group;
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }
}
