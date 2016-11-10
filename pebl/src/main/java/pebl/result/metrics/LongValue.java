package pebl.result.metrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import pebl.result.Value;

@XmlAccessorType(XmlAccessType.NONE)
public class LongValue extends Value {
    @XmlValue
    private final long value;

    LongValue() {
        this(Long.MIN_VALUE);
    }

    public LongValue(long value) {
        this.value = value;
    }


    public long getValue() {
        return value;
    }
}
