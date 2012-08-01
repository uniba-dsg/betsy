package betsy.data.assertions

import betsy.data.TestAssertion


class SoapFaultTestAssertion extends TestAssertion {
    String faultString

    @Override
    public String toString() {
        return "SoapFaultTestAssertion{" +
                "faultString='" + faultString + '\'' +
                '}';
    }
}
