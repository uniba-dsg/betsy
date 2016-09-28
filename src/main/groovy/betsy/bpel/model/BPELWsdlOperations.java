package betsy.bpel.model;

import pebl.benchmark.test.steps.soap.WsdlOperation;

public class BPELWsdlOperations {

    /**
     * The WSDL WsdlOperation startProcessSync
     */
    public static final WsdlOperation SYNC = new WsdlOperation("startProcessSync", false);
    /**
     * The WSDL WsdlOperation startProcessAsync
     */
    public static final WsdlOperation ASYNC = new WsdlOperation("startProcessAsync", true);
    /**
     * The WSDL WsdlOperation startProcessSyncString
     */
    public static final WsdlOperation SYNC_STRING = new WsdlOperation("startProcessSyncString", true);



}
