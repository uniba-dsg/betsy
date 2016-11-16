package pebl.benchmark.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.HasId;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class MetricType implements HasId {

    @XmlAttribute(required = true)
    private final ValueType dataType;

    @XmlAttribute(required = true)
    @XmlID
    private final String id;

    @XmlAttribute(required = true)
    private final String description;

    @XmlAttribute(required = true)
    private final String unit;

    public MetricType() {
        this(ValueType.STRING, "", "", "");
    }

    public MetricType(ValueType dataType, String id, String description, String unit) {
        this.dataType = Objects.requireNonNull(dataType);
        this.id = Objects.requireNonNull(id);
        this.description = Objects.requireNonNull(description);
        this.unit = Objects.requireNonNull(unit);
    }

    public ValueType getDataType() {
        return dataType;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    @Override public String toString() {
        final StringBuffer sb = new StringBuffer("MetricType{");
        sb.append("dataType=").append(dataType);
        sb.append(", id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", unit='").append(unit).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
