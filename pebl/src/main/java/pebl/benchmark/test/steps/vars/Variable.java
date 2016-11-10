package pebl.benchmark.test.steps.vars;

import java.util.Objects;

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
    private final String value;

    Variable() {
        this("","","");
    }

    public Variable(String name, String type, String value) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override public String toString() {
        return type + " " + name + " = " + value;
    }
}
