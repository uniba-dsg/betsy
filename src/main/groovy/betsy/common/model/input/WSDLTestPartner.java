package betsy.common.model.input;

public interface WSDLTestPartner extends TestPartner {

    default String getWSDLUrl() {
        return getPublishedURL() + "?wsdl";
    }

    String getPublishedURL();

}
