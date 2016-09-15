package pebl.result.metrics;

import pebl.result.Metric;

public class BooleanMetric implements Metric {

    private final boolean value;

    public BooleanMetric(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }
}
