package pebl.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

@XmlAccessorType(XmlAccessType.NONE)
public class Metric {

    @XmlAttribute(required = true)
    @XmlID
    private final String id;

    @XmlElement(required = true)
    private final ValueType type;

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String description;

    @XmlElement(required = true)
    private final String unit;

    public Metric() {
        this(new ValueType(), "", "", "", "");
    }

    public Metric(ValueType type, String name, String description, String unit, String idPrefix) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.unit = Objects.requireNonNull(unit);
        this.id = idPrefix + "-" + type + "-" + name;
    }

    public ValueType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }
}
