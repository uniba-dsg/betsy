package betsy.data.assertions;

import betsy.data.TestAssertion;

public class SoapFaultTestAssertion extends TestAssertion {
    public String getFaultString() {
        return faultString;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    private String faultString;

    @Override
    public String toString() {
        return "SoapFaultTestAssertion{" +
                "faultString='" + faultString + '\'' +
                '}';
    }
}
