package pebl.test.steps.soap;

import java.util.Objects;

/**
 * A WSDL operation of the TestInterface.wsdl file.
 */
public class WsdlOperation {

    /**
     * The name of the WSDL operation.
     */
    private final String name;
    private final boolean isOneWay;

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
