package pebl.featuretree;

class ResultFormatElement {
    private final ResultFormatMetric type;
    private final String name;
    private final String description;
    private final String unit;

    ResultFormatElement(ResultFormatMetric type, String name, String description, String unit) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.unit = unit;
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
