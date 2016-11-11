package pebl.result.metrics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import pebl.result.Value;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class BooleanValue extends Value {

    @XmlValue
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
