package pebl.result.metrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.result.Value;

@XmlAccessorType(XmlAccessType.NONE)
public class DoubleValue extends Value {

    @XmlElement(required = true)
    private final double value;

    DoubleValue() {
        this(Double.MIN_VALUE);
    }

    public DoubleValue(double value) {
        this.value = value;
    }


    public double getValue() {
        return value;
    }
}
