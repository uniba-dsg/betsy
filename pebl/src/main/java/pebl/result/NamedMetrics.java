package pebl.result;

import java.util.Map;
import java.util.Objects;

public class NamedMetrics {

    private final Map<String, Metric> values;

    public NamedMetrics(Map<String, Metric> values) {
        this.values = Objects.requireNonNull(values);
    }

    public Map<String, Metric> getValues() {
        return values;
    }
}
