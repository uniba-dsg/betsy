package pebl.test.assertions;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestAssertion;

public class SoapFaultTestAssertion extends TestAssertion {

    private final String faultString;

    SoapFaultTestAssertion() {
        this("");
    }

    public SoapFaultTestAssertion(String faultString) {
        this.faultString = faultString;
    }

    @XmlElement(required = true)
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
