package pebl.test.partner;

import pebl.test.TestPartner;

public interface WSDLTestPartner extends TestPartner {

    default String getWSDLUrl() {
        return getPublishedURL() + "?wsdl";
    }

    String getPublishedURL();

}
