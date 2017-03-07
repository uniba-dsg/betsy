package pebl.benchmark.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;
import pebl.HasId;

@XmlAccessorType(XmlAccessType.NONE)
public class MetricType implements HasId {

    @XmlAttribute(required = true)
    private final String dataType;

    @XmlAttribute(required = true)
    @XmlID
    private final String id;

    @XmlAttribute(required = true)
    private final String description;

    @XmlAttribute(required = true)
    private final String unit;

    @XmlValue
    @XmlCDATA
    private final String groovyScript;

    public MetricType() {
        this(ValueType.STRING, "", "", "", null);
    }

    public MetricType(ValueType dataType, String id, String description, String unit) {
        this(dataType, id, description, unit, null);
    }

    public MetricType(ValueType dataType, String id, String description, String unit, String groovyScript) {
        this.dataType = Objects.requireNonNull(dataType.name().toLowerCase());
        this.id = Objects.requireNonNull(id);
        this.description = Objects.requireNonNull(description);
        this.unit = Objects.requireNonNull(unit);
        this.groovyScript = groovyScript;
    }

    public String getGroovyScript() {
        return groovyScript;
    }

    public ValueType getDataType() {
        return ValueType.valueOf(dataType.toUpperCase());
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

    @Override
    public String toString() {
        return getId();
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MetricType that = (MetricType) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode() {
        return Objects.hash(id);
    }
}
