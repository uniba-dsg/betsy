package pebl.benchmark.test.steps;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.benchmark.test.TestStep;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class DelayTesting extends TestStep {
    /**
     * Time to wait/delay further test execution after processing this step in milliseconds.
     */
    @XmlElement(required = true)
    private Integer milliseconds;

    @Override
    public String toString() {
        return "DelayTestStep{description=" + getDescription() + ", timeToWaitAfterwards=" + milliseconds + "}";
    }

    public Integer getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Integer milliseconds) {
        this.milliseconds = milliseconds;
    }
}
