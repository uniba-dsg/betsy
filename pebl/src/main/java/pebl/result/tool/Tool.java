package pebl.result.tool;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import pebl.HasID;
import pebl.HasName;

@XmlAccessorType(XmlAccessType.NONE)
public class Tool implements HasID, HasName {

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)

    private final String version;

    public Tool() {
        this("", "");
    }

    public Tool(String name, String version) {
        this.name = Objects.requireNonNull(name);
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public String getName() {
        return name;
    }

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
