package pebl.feature;

import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.HasID;
import pebl.HasName;

@XmlAccessorType(XmlAccessType.NONE)
public class Feature implements HasID, HasName {

    private final FeatureSet featureSet;
    private final String name;
    private final String description;

    private final String upperBound;

    public Feature() {
        this(new FeatureSet(), "");
    }

    public Feature(FeatureSet featureSet, String name) {
        this(featureSet, name, "");
    }

    public Feature(FeatureSet featureSet, String name, String description) {
        this.featureSet = Objects.requireNonNull(featureSet);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.upperBound = "";

        this.featureSet.addFeature(this);
    }

    public Feature(FeatureSet featureSet, String name, String description, String upperBound) {
        this.featureSet = Objects.requireNonNull(featureSet);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.upperBound = Objects.requireNonNull(upperBound);

        this.featureSet.addFeature(this);
    }

    @Override
    @XmlID
    @XmlElement(required = true)
    public String getID() {
        return String.join(HasID.SEPARATOR, featureSet.getID(), name);
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
        Feature feature = (Feature) o;
        return Objects.equals(getID(), feature.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }

    @XmlIDREF
    public FeatureSet getFeatureSet() {
        return featureSet;
    }

    @XmlElement
    public String getUpperBound() {
        return upperBound;
    }
}
