package pebl.test.assertions;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestAssertion;

public class XpathTestAssertion extends TestAssertion {

    private final String xpathExpression;
    private final String expectedOutput;

    XpathTestAssertion() {
        this("", "");
    }

    public XpathTestAssertion(String xpathExpression, String expectedOutput) {
        this.xpathExpression = Objects.requireNonNull(xpathExpression);
        this.expectedOutput = Objects.requireNonNull(expectedOutput);
    }

    @Override
    public String toString() {
        return "XpathTestAssertion{" + "xpathExpression='" + xpathExpression + "\'" + ", expectedOutput='" + expectedOutput + "\'" + "}";
    }

    @XmlElement(required = true)
    public String getXpathExpression() {
        return xpathExpression;
    }

    @XmlElement(required = true)
    public String getExpectedOutput() {
        return expectedOutput;
    }

}
