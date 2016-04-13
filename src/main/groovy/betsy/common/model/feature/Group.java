package betsy.common.model.feature;

import betsy.common.HasName;
import betsy.common.model.HasID;
import betsy.common.model.ProcessLanguage;

import java.util.Objects;

public class Group implements HasID, HasName {

    public final String name;
    public final ProcessLanguage processLanguage;
    public final String description;

    public Group(String name, ProcessLanguage processLanguage, String description) {
        this.name = Objects.requireNonNull(name);
        this.processLanguage = Objects.requireNonNull(processLanguage);
        this.description = Objects.requireNonNull(description);
    }

    public String getID() {
        return String.join(SEPARATOR, processLanguage.name(), name);
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
