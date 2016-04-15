package betsy.common.model.feature;

import java.util.Objects;

import betsy.common.HasName;
import betsy.common.model.HasID;

public class Language implements HasID, HasName {

    public final Capability capability;
    public final String name;

    public Language(Capability capability, String name) {
        this.capability = Objects.requireNonNull(capability);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getID() {
        return String.join(SEPARATOR, capability.getID(), name);
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
        Language construct = (Language) o;
        return Objects.equals(getID(), construct.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

}
