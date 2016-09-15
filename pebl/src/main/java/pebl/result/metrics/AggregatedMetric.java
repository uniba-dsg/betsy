package pebl.result.metrics;

import java.math.BigDecimal;
import java.util.Objects;

import pebl.result.Metric;

class AggregatedMetric implements Metric {
    private final BigDecimal min;
    private final BigDecimal max;
    private final BigDecimal standardDeviation;
    private final BigDecimal average;
    private final BigDecimal relativeStandardDeviation;

    AggregatedMetric(BigDecimal min,
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
