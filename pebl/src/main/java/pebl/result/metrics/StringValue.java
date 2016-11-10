package pebl.result.metrics;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import pebl.result.Value;

@XmlAccessorType(XmlAccessType.NONE)
public class StringValue extends Value {

    public static final StringValue PLUS = new StringValue("+");
    public static final StringValue PLUS_MINUS = new StringValue("+/-");
    public static final StringValue MINUS = new StringValue("-");

    @XmlValue
    private final String value;

    StringValue() {
        this("");
    }

    public StringValue(String value) {
        this.value = Objects.requireNonNull(value);
    }


    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
