package betsy.common.model.feature;

import betsy.common.HasName;
import betsy.common.model.HasID;

import java.util.Objects;

public class Construct implements HasID, HasName {

    public final Group group;
    public final String name;
    public final String description;

    public Construct(Group group, String name) {
        this(group, name, "");
    }

    public Construct(Group group, String name, String description) {
        this.group = Objects.requireNonNull(group);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
    }

    @Override
    public String getID() {
        return String.join(SEPARATOR, group.getID(), name);
    }

    @Override
    public String getName() {
        return name;
    }
}
