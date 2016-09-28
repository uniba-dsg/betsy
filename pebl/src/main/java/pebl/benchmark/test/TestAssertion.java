package pebl.benchmark.test;

import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.benchmark.test.assertions.ExitAssertion;
import pebl.benchmark.test.assertions.SoapFaultTestAssertion;
import pebl.benchmark.test.assertions.TraceTestAssertion;
import pebl.benchmark.test.assertions.XpathTestAssertion;

@XmlSeeAlso({ExitAssertion.class, SoapFaultTestAssertion.class, TraceTestAssertion.class, XpathTestAssertion.class})
public class TestAssertion {
}
