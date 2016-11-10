package pebl.benchmark.feature;

import java.util.Objects;

import javax.xml.bind.annotation.XmlValue;

/**
 * Idea: only primitive types, aggregated as a special type (could be removed as well)
 */
public class ValueType {

    public static final ValueType BOOLEAN = new ValueType("BOOLEAN");
    public static final ValueType LONG = new ValueType("LONG");
    public static final ValueType STRING = new ValueType("STRING");
    public static final ValueType DOUBLE = new ValueType("DOUBLE");
    public static final ValueType AGGREGATED = new ValueType("AGGREGATED");

    @XmlValue
    private final String dataType;

    ValueType() {
        this("");
    }

    public ValueType(String dataType) {
        this.dataType = Objects.requireNonNull(dataType);
    }

    public String getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return dataType;
    }
}
