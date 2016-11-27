package peal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Variable {

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String type;

    @XmlElement(required = true)
    private final Object value;

    public Variable() {
        this("","",new Object());
    }

    public Variable(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
