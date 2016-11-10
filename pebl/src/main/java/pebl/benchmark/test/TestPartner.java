package pebl.benchmark.test;

import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.benchmark.test.partner.RuleBasedWSDLTestPartner;
import pebl.benchmark.test.partner.NoTestPartner;
import pebl.benchmark.test.partner.ScriptBasedWSDLTestPartner;

@XmlSeeAlso({RuleBasedWSDLTestPartner.class, NoTestPartner.class, ScriptBasedWSDLTestPartner.class})
public class TestPartner {

}
