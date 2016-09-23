package pebl.result.metrics;

import java.math.BigDecimal;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.result.Metric;

@XmlAccessorType(XmlAccessType.NONE)
class AggregatedMetric extends Metric {

    private static final BigDecimal MIN = BigDecimal.valueOf(Double.MIN_VALUE);

    @XmlElement(required = true)
    private final BigDecimal min;

    @XmlElement(required = true)
    private final BigDecimal max;

    @XmlElement(required = true)
    private final BigDecimal standardDeviation;

    @XmlElement(required = true)
    private final BigDecimal average;

    @XmlElement(required = true)
    private final BigDecimal relativeStandardDeviation;

    AggregatedMetric() {
        this(MIN, MIN, MIN, MIN, MIN);
    }

    public AggregatedMetric(BigDecimal min,
            BigDecimal max,
            BigDecimal standardDeviation,
            BigDecimal average,
            BigDecimal relativeStandardDeviation) {
        this.min = Objects.requireNonNull(min);
        this.max = Objects.requireNonNull(max);
        this.standardDeviation = Objects.requireNonNull(standardDeviation);
        this.average = Objects.requireNonNull(average);
        this.relativeStandardDeviation = Objects.requireNonNull(relativeStandardDeviation);
    }


    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getStandardDeviation() {
        return standardDeviation;
    }

    public BigDecimal getAverage() {
        return average;
    }

    public BigDecimal getRelativeStandardDeviation() {
        return relativeStandardDeviation;
    }
}
