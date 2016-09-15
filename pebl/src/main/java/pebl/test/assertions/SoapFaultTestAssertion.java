package pebl.test.assertions;

import pebl.test.TestAssertion;

public class SoapFaultTestAssertion extends TestAssertion {

    private final String faultString;

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
