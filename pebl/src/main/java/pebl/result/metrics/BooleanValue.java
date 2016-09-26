package pebl.result.metrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.result.Value;

@XmlAccessorType(XmlAccessType.NONE)
public class BooleanValue extends Value {
    @XmlElement(required = true)
    private final boolean value;

    BooleanValue() {
        this(false);
    }

    public BooleanValue(boolean value) {
        this.value = value;
    }


    public boolean isValue() {
        return value;
    }
}
