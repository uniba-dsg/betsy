package betsy.data.assertions

import betsy.data.TestAssertion


class XpathTestAssertion extends TestAssertion {
    String xpathExpression
    String expectedOutput
    String output

    @Override
    public String toString() {
        return "XpathTestAssertion{" +
                "xpathExpression='" + xpathExpression + '\'' +
                ", expectedOutput='" + expectedOutput + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}
