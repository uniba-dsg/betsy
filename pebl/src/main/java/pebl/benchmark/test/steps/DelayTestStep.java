package pebl.benchmark.test.steps;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pebl.benchmark.test.TestStep;

@XmlAccessorType(XmlAccessType.NONE)
public class DelayTestStep extends TestStep {
    /**
     * Time to wait/delay further test execution after processing this step in milliseconds.
     */
    @XmlElement(required = true)
    private Integer timeToWaitAfterwards;

    @Override
    public String toString() {
        return "DelayTestStep{description=" + getDescription() + ", timeToWaitAfterwards=" + timeToWaitAfterwards + "}";
    }

    public Integer getTimeToWaitAfterwards() {
        return timeToWaitAfterwards;
    }

    public void setTimeToWaitAfterwards(Integer timeToWaitAfterwards) {
        this.timeToWaitAfterwards = timeToWaitAfterwards;
    }
}
