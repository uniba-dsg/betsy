package pebl.benchmark.test.assertions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.benchmark.test.TestAssertion;

@XmlAccessorType(XmlAccessType.NONE)
public class AssertSoapFault extends TestAssertion {

    @XmlElement(required = true)
    private final String faultString;

    AssertSoapFault() {
        this("");
    }

    public AssertSoapFault(String faultString) {
        this.faultString = faultString;
    }

    public String getFaultString() {
        return faultString;
    }

    @Override
    public String toString() {
        return "SoapFaultTestAssertion{" +
                "faultString='" + faultString + '\'' +
                '}';
    }
}
