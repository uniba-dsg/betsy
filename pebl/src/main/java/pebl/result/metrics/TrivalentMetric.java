package pebl.result.metrics;

import java.util.Objects;

import pebl.result.Metric;

public enum TrivalentMetric implements Metric {

    PLUS("+"), PLUS_MINUS("+/-"), MINUS("-");

    private final String serialization;

    TrivalentMetric(String serialization) {
        this.serialization = Objects.requireNonNull(serialization);
    }

    public String getSerialization() {
        return serialization;
    }

    @Override
    public String toString() {
        return serialization;
    }
}
