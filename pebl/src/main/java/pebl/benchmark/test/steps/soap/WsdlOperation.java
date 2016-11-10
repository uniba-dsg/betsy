package pebl.benchmark.test.steps.soap;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * A WSDL operation of the TestInterface.wsdl file.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class WsdlOperation {

    /**
     * The name of the WSDL operation.
     */
    @XmlAttribute(required = true)
    private final String name;

    @XmlAttribute(required = true)
    private final boolean isOneWay;

    WsdlOperation() {
        this("", false);
    }

    public WsdlOperation(String name, boolean isOneWay) {
        this.name = Objects.requireNonNull(name);
        this.isOneWay = isOneWay;
    }

    @Override
    public String toString() {
        return "WsdlOperation{" + "name='" + name + "\'" + "}";
    }

    public final String getName() {
        return name;
    }

    public boolean isOneWay() {
        return isOneWay;
    }
}
