package pebl.feature;

import java.util.Objects;
import java.util.Optional;

import pebl.HasName;
import pebl.HasID;

public class Feature implements HasID, HasName {

    private final FeatureSet featureSet;
    private final String name;
    private final String description;

    private final Optional<String> upperBound;

    public Feature(FeatureSet featureSet, String name) {
        this(featureSet, name, "");
    }

    public Feature(FeatureSet featureSet, String name, String description) {
        this.featureSet = Objects.requireNonNull(featureSet);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.upperBound = Optional.empty();

        this.featureSet.addFeature(this);
    }

    public Feature(FeatureSet featureSet, String name, String description, String upperBound) {
        this.featureSet = Objects.requireNonNull(featureSet);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.upperBound = Optional.of(upperBound);

        this.featureSet.addFeature(this);
    }

    @Override
    public String getID() {
        return String.join(HasID.SEPARATOR, featureSet.getID(), name);
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

    public String getDescription() {
        return description;
    }

    public FeatureSet getFeatureSet() {
        return featureSet;
    }

    public Optional<String> getUpperBound() {
        return upperBound;
    }
}
