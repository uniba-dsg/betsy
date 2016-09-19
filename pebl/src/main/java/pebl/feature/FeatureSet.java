package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pebl.HasName;
import pebl.HasID;

public class FeatureSet implements HasID, HasName {

    private final Group group;
    private final String name;
    private final String description;
    private final List<Feature> features = new LinkedList<>();

    public FeatureSet(Group group, String name) {
        this(group, name, "");
    }

    public FeatureSet(Group group, String name, String description) {
        this.group = Objects.requireNonNull(group);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);

        this.group.addFeatureSet(this);
    }

    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    void addFeature(Feature feature) {
        this.features.add(feature);
    }

    @Override
    public String getID() {
        return String.join(HasID.SEPARATOR, group.getID(), name);
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
