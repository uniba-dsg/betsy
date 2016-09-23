package pebl.test;

import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.test.assertions.ExitAssertion;
import pebl.test.assertions.SoapFaultTestAssertion;
import pebl.test.assertions.TraceTestAssertion;
import pebl.test.assertions.XpathTestAssertion;

@XmlSeeAlso({ExitAssertion.class, SoapFaultTestAssertion.class, TraceTestAssertion.class, XpathTestAssertion.class})
public class TestAssertion {
}
