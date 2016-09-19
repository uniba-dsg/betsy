package pebl.tool;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.HasID;
import pebl.HasName;

@XmlAccessorType(XmlAccessType.NONE)
public class Tool implements HasID, HasName {

    private final String name;

    private final String version;

    public Tool() {
        this("", "");
    }

    public Tool(String name, String version) {
        this.name = Objects.requireNonNull(name);
        this.version = Objects.requireNonNull(version);
    }

    @Override
    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    @XmlElement(required = true)
    public String getVersion() {
        return version;
    }

    @Override
    @XmlAttribute(required = true)
    @XmlID
    public String getID() {
        return String.join(SEPARATOR, name, version);
    }
}
