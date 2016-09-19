package pebl.result.metrics;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import pebl.result.Metric;

public class TrivalentMetric extends Metric {

    public static final TrivalentMetric PLUS = new TrivalentMetric("+");
    public static final TrivalentMetric PLUS_MINUS = new TrivalentMetric("+/-");
    public static final TrivalentMetric MINUS = new TrivalentMetric("-");

    private final String serialization;

    TrivalentMetric() {
        this("");
    }

    public TrivalentMetric(String serialization) {
        this.serialization = Objects.requireNonNull(serialization);
    }

    @XmlElement(required = true)
    public String getSerialization() {
        return serialization;
    }

    @Override
    public String toString() {
        return serialization;
    }
}
