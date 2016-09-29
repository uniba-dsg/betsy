package pebl.benchmark.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Idea: only primitive types, aggregated as a special type (could be removed as well)
 */
public class ValueType {

    public static final ValueType BOOLEAN = new ValueType("BOOLEAN");
    public static final ValueType LONG = new ValueType("LONG");
    public static final ValueType STRING = new ValueType("STRING");
    public static final ValueType DOUBLE = new ValueType("DOUBLE");
    public static final ValueType AGGREGATED = new ValueType("AGGREGATED");

    @XmlElement(required = true)
    private final String type;

    ValueType() {
        this("");
    }

    public ValueType(String type) {
        this.type = Objects.requireNonNull(type);
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
