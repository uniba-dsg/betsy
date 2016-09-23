package pebl.result.metrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.result.Metric;

@XmlAccessorType(XmlAccessType.NONE)
public class IntegerMetric extends Metric {
    @XmlElement(required = true)
    private final int value;

    IntegerMetric() {
        this(Integer.MIN_VALUE);
    }

    public IntegerMetric(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
