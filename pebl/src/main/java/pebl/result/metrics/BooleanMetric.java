package pebl.result.metrics;

import javax.xml.bind.annotation.XmlElement;

import pebl.result.Metric;

public class BooleanMetric extends Metric {

    private final boolean value;

    BooleanMetric() {
        this(false);
    }

    public BooleanMetric(boolean value) {
        this.value = value;
    }

    @XmlElement(required = true)
    public boolean isValue() {
        return value;
    }
}
