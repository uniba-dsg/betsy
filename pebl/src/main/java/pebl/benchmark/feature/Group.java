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
public class Group implements HasID, HasName {

    @XmlElement(required = true)
    private final String name;

    @XmlInverseReference(mappedBy="groups")
    private final Language language;

    @XmlElement(required = true)
    private final String description;

    @XmlElement(name = "featureSet")
    private final List<FeatureSet> featureSets = new LinkedList<>();

    @XmlID
    @XmlAttribute(required = true)
    private final String id;

    @XmlElement
    private final List<Metric> metrics = new LinkedList<>();

    public Group addMetric(ScriptMetricType scriptMetricType) {
        metrics.add(new Metric(scriptMetricType, getID()));

        return this;
    }

    public Group() {
        this("", new Language(), "");
    }

    public Group(String name, Language language, String description) {
        this.name = Objects.requireNonNull(name);
        this.language = Objects.requireNonNull(language);
        this.description = Objects.requireNonNull(description);

        this.id = String.join(HasID.SEPARATOR, language.getID(), name);

        this.language.addGroup(this);
    }

    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return "Group " + getID();
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
        return Objects.equals(getID(), group.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    void addFeatureSet(FeatureSet featureSet) {
        this.featureSets.add(featureSet);
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
}
