package pebl.test;

import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.test.partner.ExternalWSDLTestPartner;
import pebl.test.partner.InternalWSDLTestPartner;
import pebl.test.partner.NoTestPartner;

@XmlSeeAlso({ExternalWSDLTestPartner.class, InternalWSDLTestPartner.class, NoTestPartner.class})
public class TestPartner {

}
