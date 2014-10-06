package betsy.model.assertions;

import betsy.model.TestAssertion;

public class XpathTestAssertion extends TestAssertion {
    private String xpathExpression;
    private String expectedOutput;
    private String output;

    @Override
    public String toString() {
        return "XpathTestAssertion{" + "xpathExpression='" + xpathExpression + "\'" + ", expectedOutput='" + expectedOutput + "\'" + ", output='" + output + "\'" + "}";
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
