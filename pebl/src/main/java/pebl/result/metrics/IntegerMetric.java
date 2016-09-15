package pebl.result.metrics;

import pebl.result.Metric;

public class IntegerMetric implements Metric {

    private final int value;

    public IntegerMetric(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
