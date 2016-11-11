package pebl.benchmark.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.HasId;

@XmlAccessorType(XmlAccessType.NONE)
public class Metric implements HasId {

    @XmlAttribute(required = true)
    @XmlID
    private final String id;

    @XmlAttribute(required = true)
    @XmlIDREF
    private final MetricType metricType;

    public Metric() {
        this(new MetricType(), "");
    }

    public Metric(MetricType metricType, String idPrefix) {
        this.metricType = Objects.requireNonNull(metricType);
        this.id = String.join(HasId.SEPARATOR, idPrefix, metricType.getId());
    }

    public String getId() {
        return id;
    }

    public MetricType getMetricType() {
        return metricType;
    }
}
