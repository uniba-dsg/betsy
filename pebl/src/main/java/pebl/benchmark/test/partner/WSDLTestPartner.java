package pebl.benchmark.test.partner;

import javax.xml.bind.annotation.XmlElement;

import pebl.benchmark.test.TestPartner;

public abstract class WSDLTestPartner extends TestPartner {

    @XmlElement(required = true)
    public String getWSDLUrl() {
        return getPublishedURL() + "?wsdl";
    }

    @XmlElement(required = true)
    public abstract String getPublishedURL();

}
