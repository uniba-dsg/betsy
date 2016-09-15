package pebl.featuretree;

import java.util.Objects;

import pebl.HasName;
import pebl.HasID;

public class Capability implements HasID, HasName {

    private final String name;

    public Capability(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getID() {
        return name;
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
        Capability construct = (Capability) o;
        return Objects.equals(getID(), construct.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }
}
