package pebl.result.metrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.result.Metric;

@XmlAccessorType(XmlAccessType.NONE)
public class BooleanMetric extends Metric {
    @XmlElement(required = true)
    private final boolean value;

    BooleanMetric() {
        this(false);
    }

    public BooleanMetric(boolean value) {
        this.value = value;
    }


    public boolean isValue() {
        return value;
    }
}
