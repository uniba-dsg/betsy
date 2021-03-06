package pebl.benchmark.test.assertions;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.benchmark.test.TestAssertion;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class AssertXpath extends TestAssertion {

    @XmlAttribute(required = true)
    private final String xpathExpression;

    @XmlAttribute(required = true)
    private final String expectedOutput;

    AssertXpath() {
        this("", "");
    }

    public AssertXpath(String xpathExpression, String expectedOutput) {
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
