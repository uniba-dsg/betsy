package pebl.result.metrics;

import javax.xml.bind.annotation.XmlElement;

import pebl.result.Metric;

public class IntegerMetric extends Metric {

    private final int value;

    IntegerMetric() {
        this(Integer.MIN_VALUE);
    }

    public IntegerMetric(int value) {
        this.value = value;
    }

    @XmlElement(required = true)
    public int getValue() {
        return value;
    }
}
