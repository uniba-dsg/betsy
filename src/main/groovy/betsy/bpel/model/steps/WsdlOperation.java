package betsy.bpel.model.steps;

/**
 * A WSDL operation of the TestInterface.wsdl file.
 */
public class WsdlOperation {
    /**
     * The WSDL WsdlOperation startProcessSync
     */
    public static final WsdlOperation SYNC = new WsdlOperation("startProcessSync");
    /**
     * The WSDL WsdlOperation startProcessAsync
     */
    public static final WsdlOperation ASYNC = new WsdlOperation("startProcessAsync");
    /**
     * The WSDL WsdlOperation startProcessSyncString
     */
    public static final WsdlOperation SYNC_STRING = new WsdlOperation("startProcessSyncString");
    /**
     * The name of the WSDL operation.
     */
    private final String name;

    public WsdlOperation(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WsdlOperation{" + "name='" + name + "\'" + "}";
    }

    public final String getName() {
        return name;
    }
}
