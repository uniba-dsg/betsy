package betsy.data

/**
 * A WSDL operation of the TestInterface.wsdl file.
 */
class WsdlOperation {

    /**
     * The WSDL WsdlOperation startProcessSync
     */
    public final static WsdlOperation SYNC = new WsdlOperation(name: "startProcessSync")

    /**
     * The WSDL WsdlOperation startProcessAsync
     */
    public final static WsdlOperation ASYNC = new WsdlOperation(name: "startProcessAsync")

    /**
     * The name of the WSDL operation.
     */
    String name

    @Override
    public String toString() {
        return "WsdlOperation{" +
                "name='" + name + '\'' +
                '}';
    }
}
