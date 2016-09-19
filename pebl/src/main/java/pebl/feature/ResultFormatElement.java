package pebl.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ResultFormatElement {
    private final ResultFormatMetric type;
    private final String name;
    private final String description;
    private final String unit;

    ResultFormatElement() {
        this(null, "", "", "");
    }

    public ResultFormatElement(ResultFormatMetric type, String name, String description, String unit) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.unit = Objects.requireNonNull(unit);
    }

    @XmlElement(required = true)
    public ResultFormatMetric getType() {
        return type;
    }

    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    @XmlElement(required = true)
    public String getDescription() {
        return description;
    }

    @XmlElement(required = true)
    public String getUnit() {
        return unit;
    }
}
