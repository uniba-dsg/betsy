package betsy.common.model.feature;

import java.util.Objects;
import java.util.Optional;

import betsy.common.HasName;
import betsy.common.model.HasID;

public class Feature implements HasID, HasName {

    public final FeatureSet featureSet;
    public final String name;
    public final String description;

    public final Optional<String> upperBound;

    public Feature(FeatureSet featureSet, String name) {
        this(featureSet, name, "");
    }

    public Feature(FeatureSet featureSet, String name, String description) {
        this.featureSet = Objects.requireNonNull(featureSet);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.upperBound = Optional.empty();
    }

    public Feature(FeatureSet featureSet, String name, String description, String upperBound) {
        this.featureSet = Objects.requireNonNull(featureSet);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.upperBound = Optional.of(upperBound);
    }

    @Override
    public String getID() {
        return String.join(SEPARATOR, featureSet.getID(), name);
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
        Feature feature = (Feature) o;
        return Objects.equals(getID(), feature.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }
}
