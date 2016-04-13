package betsy.common.model.feature;

import betsy.common.HasName;
import betsy.common.model.HasID;
import com.jniwrapper.Const;

import java.util.Objects;

public class Feature implements HasID, HasName {

    public final Construct construct;
    public final String name;
    public final String description;

    public Feature(Construct construct, String name) {
        this(construct, name, "");
    }

    public Feature(Construct construct, String name, String description) {
        this.construct = Objects.requireNonNull(construct);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
    }

    @Override
    public String getID() {
        return String.join(SEPARATOR, construct.getID(), name);
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
