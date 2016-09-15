package pebl.featuretree;

import java.util.Objects;

import pebl.HasName;
import pebl.HasID;

public class Group implements HasID, HasName {

    public final String name;
    public final Language language;
    public final String description;

    public Group(String name, Language language, String description) {
        this.name = Objects.requireNonNull(name);
        this.language = Objects.requireNonNull(language);
        this.description = Objects.requireNonNull(description);
    }

    public String getID() {
        return String.join(HasID.SEPARATOR, language.getID(), name);
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
}
