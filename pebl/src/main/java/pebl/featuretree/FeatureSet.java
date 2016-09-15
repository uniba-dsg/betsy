package pebl.featuretree;

import java.util.Objects;

import pebl.HasName;
import pebl.HasID;

public class FeatureSet implements HasID, HasName {

    public final Group group;
    public final String name;
    public final String description;

    public FeatureSet(Group group, String name) {
        this(group, name, "");
    }

    public FeatureSet(Group group, String name, String description) {
        this.group = Objects.requireNonNull(group);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
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
}
