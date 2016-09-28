package pebl.result.metrics;

import java.math.BigDecimal;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.result.Value;

@XmlAccessorType(XmlAccessType.NONE)
public class AggregatedValue extends Value {

    private static final double MIN = Double.MIN_VALUE;

    @XmlElement(required = true)
    private final double min;

    @XmlElement(required = true)
    private final double max;

    @XmlElement(required = true)
    private final double standardDeviation;

    @XmlElement(required = true)
    private final double average;

    @XmlElement(required = true)
    private final double relativeStandardDeviation;

    AggregatedValue() {
        this(MIN, MIN, MIN, MIN, MIN);
    }

    public AggregatedValue(double min,
            double max,
            double standardDeviation,
            double average,
            double relativeStandardDeviation) {
        this.min = Objects.requireNonNull(min);
        this.max = Objects.requireNonNull(max);
        this.standardDeviation = Objects.requireNonNull(standardDeviation);
        this.average = Objects.requireNonNull(average);
        this.relativeStandardDeviation = Objects.requireNonNull(relativeStandardDeviation);
    }


    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getAverage() {
        return average;
    }

    public double getRelativeStandardDeviation() {
        return relativeStandardDeviation;
    }
}
