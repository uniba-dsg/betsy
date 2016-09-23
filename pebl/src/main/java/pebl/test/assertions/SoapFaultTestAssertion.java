package pebl.test.assertions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestAssertion;

@XmlAccessorType(XmlAccessType.NONE)
public class SoapFaultTestAssertion extends TestAssertion {

    @XmlElement(required = true)
    private final String faultString;

    SoapFaultTestAssertion() {
        this("");
    }

    public SoapFaultTestAssertion(String faultString) {
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
