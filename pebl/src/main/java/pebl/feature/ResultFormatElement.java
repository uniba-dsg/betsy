package pebl.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ResultFormatElement {

    @XmlElement(required = true)
    private final ResultFormatMetric type;

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String description;

    @XmlElement(required = true)
    private final String unit;

    ResultFormatElement() {
        this(new ResultFormatMetric(), "", "", "");
    }

    public ResultFormatElement(ResultFormatMetric type, String name, String description, String unit) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.unit = Objects.requireNonNull(unit);
    }

    public ResultFormatMetric getType() {
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
