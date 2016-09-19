package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.HasName;
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class Language implements HasID, HasName {

    private final Capability capability;
    private final String name;
    private final List<Group> groups = new LinkedList<>();

    Language() {
        this(new Capability(), "");
    }

    public Language(Capability capability, String name) {
        this.capability = Objects.requireNonNull(capability);
        this.name = Objects.requireNonNull(name);

        this.capability.addLanguage(this);
    }

    @Override
    @XmlID
    @XmlElement(required = true)
    public String getID() {
        return String.join(HasID.SEPARATOR, capability.getID(), name);
    }

    @Override
    @XmlElement(required = true)
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

    @XmlIDREF
    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    @XmlIDREF
    @XmlElement(required = true)
    public Capability getCapability() {
        return capability;
    }

}
