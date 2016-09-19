package pebl.test.assertions;

import java.util.Objects;

import pebl.test.TestAssertion;

public class XpathTestAssertion extends TestAssertion {

    private final String xpathExpression;
    private final String expectedOutput;

    public XpathTestAssertion(String xpathExpression, String expectedOutput) {
        this.xpathExpression = Objects.requireNonNull(xpathExpression);
        this.expectedOutput = Objects.requireNonNull(expectedOutput);
    }

    @Override
    public String toString() {
        return "XpathTestAssertion{" + "xpathExpression='" + xpathExpression + "\'" + ", expectedOutput='" + expectedOutput + "\'" + "}";
    }

    public String getXpathExpression() {
        return xpathExpression;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

}
