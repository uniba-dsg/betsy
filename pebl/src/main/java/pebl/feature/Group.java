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

import pebl.HasName;
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class Group implements HasID, HasName {

    private final String name;
    private final Language language;
    private final String description;
    private final List<FeatureSet> featureSets = new LinkedList<>();

    public Group() {
        this("", new Language(), "");
    }

    public Group(String name, Language language, String description) {
        this.name = Objects.requireNonNull(name);
        this.language = Objects.requireNonNull(language);
        this.description = Objects.requireNonNull(description);

        this.language.addGroup(this);
    }

    @XmlAttribute(required = true)
    public String getID() {
        return String.join(HasID.SEPARATOR, language.getID(), name);
    }

    @Override
    public String toString() {
        return "Group " + getID();
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
        Group group = (Group) o;
        return Objects.equals(getID(), group.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    void addFeatureSet(FeatureSet featureSet) {
        this.featureSets.add(featureSet);
    }

    @XmlElement(name="featureSet")
    public List<FeatureSet> getFeatureSets() {
        return Collections.unmodifiableList(featureSets);
    }

    @XmlTransient
    public Language getLanguage() {
        return language;
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }
}
