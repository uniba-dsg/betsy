package pebl.feature;

import java.util.Objects;

public class ResultFormatElement {
    private final ResultFormatMetric type;
    private final String name;
    private final String description;
    private final String unit;

    public ResultFormatElement(ResultFormatMetric type, String name, String description, String unit) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.unit = Objects.requireNonNull(unit);
    }

    public ResultFormatMetric getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }
}
