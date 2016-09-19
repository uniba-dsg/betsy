package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pebl.HasName;
import pebl.HasID;

public class Language implements HasID, HasName {

    private final Capability capability;
    private final String name;
    private final List<Group> groups = new LinkedList<>();

    public Language(Capability capability, String name) {
        this.capability = Objects.requireNonNull(capability);
        this.name = Objects.requireNonNull(name);

        this.capability.addLanguage(this);
    }

    @Override
    public String getID() {
        return String.join(HasID.SEPARATOR, capability.getID(), name);
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

    void addGroup(Group group) {
        this.groups.add(group);
    }

    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public Capability getCapability() {
        return capability;
    }

}
