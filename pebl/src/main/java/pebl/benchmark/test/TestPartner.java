package pebl.benchmark.test;

import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.benchmark.test.partner.ExternalWSDLTestPartner;
import pebl.benchmark.test.partner.InternalWSDLTestPartner;
import pebl.benchmark.test.partner.NoTestPartner;

@XmlSeeAlso({ExternalWSDLTestPartner.class, InternalWSDLTestPartner.class, NoTestPartner.class})
public class TestPartner {

}
