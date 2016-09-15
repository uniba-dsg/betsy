package pebl.test.assertions;

import pebl.test.TestAssertion;

public class XpathTestAssertion extends TestAssertion {
    private String xpathExpression;
    private String expectedOutput;

    @Override
    public String toString() {
        return "XpathTestAssertion{" + "xpathExpression='" + xpathExpression + "\'" + ", expectedOutput='" + expectedOutput + "\'" + "}";
    }

    public String getXpathExpression() {
        return xpathExpression;
    }

    public void setXpathExpression(String xpathExpression) {
        this.xpathExpression = xpathExpression;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

}
