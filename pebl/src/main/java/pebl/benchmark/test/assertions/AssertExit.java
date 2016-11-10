package pebl.benchmark.test.assertions;

import javax.xml.bind.annotation.XmlRootElement;

import pebl.benchmark.test.TestAssertion;

@XmlRootElement
public class AssertExit extends TestAssertion {
    @Override
    public String toString() {
        return "ExitAssertion";
    }

}
