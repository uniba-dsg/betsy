package pebl.result.metrics;

import java.math.BigDecimal;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import pebl.result.Metric;

class AggregatedMetric extends Metric {

    private static final BigDecimal MIN = BigDecimal.valueOf(Double.MIN_VALUE);
    private final BigDecimal min;
    private final BigDecimal max;
    private final BigDecimal standardDeviation;
    private final BigDecimal average;
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

    @XmlElement(required = true)
    public BigDecimal getMin() {
        return min;
    }

    @XmlElement(required = true)
    public BigDecimal getMax() {
        return max;
    }

    @XmlElement(required = true)
    public BigDecimal getStandardDeviation() {
        return standardDeviation;
    }

    @XmlElement(required = true)
    public BigDecimal getAverage() {
        return average;
    }

    @XmlElement(required = true)
    public BigDecimal getRelativeStandardDeviation() {
        return relativeStandardDeviation;
    }
}
