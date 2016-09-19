package pebl.test.partner;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestPartner;

public abstract class WSDLTestPartner extends TestPartner {

    @XmlElement(required = true)
    public String getWSDLUrl() {
        return getPublishedURL() + "?wsdl";
    }

    @XmlElement(required = true)
    public abstract String getPublishedURL();

}
