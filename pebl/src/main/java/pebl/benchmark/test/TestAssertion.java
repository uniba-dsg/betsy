package pebl.benchmark.test;

import javax.xml.bind.annotation.XmlSeeAlso;

import pebl.benchmark.test.assertions.AssertDeployed;
import pebl.benchmark.test.assertions.AssertExit;
import pebl.benchmark.test.assertions.AssertNotDeployed;
import pebl.benchmark.test.assertions.AssertSoapFault;
import pebl.benchmark.test.assertions.AssertTrace;
import pebl.benchmark.test.assertions.AssertXpath;

@XmlSeeAlso({AssertExit.class, AssertSoapFault.class, AssertTrace.class, AssertXpath.class, AssertNotDeployed.class, AssertDeployed.class})
public class TestAssertion {
}
