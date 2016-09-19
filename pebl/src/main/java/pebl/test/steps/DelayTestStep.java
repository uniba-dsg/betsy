package pebl.test.steps;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.TestStep;

public class DelayTestStep extends TestStep {
    /**
     * Time to wait/delay further test execution after processing this step in milliseconds.
     */
    private Integer timeToWaitAfterwards;

    @Override
    public String toString() {
        return "DelayTestStep{description=" + getDescription() + ", timeToWaitAfterwards=" + timeToWaitAfterwards + "}";
    }

    @XmlElement(required = true)
    public Integer getTimeToWaitAfterwards() {
        return timeToWaitAfterwards;
    }

    public void setTimeToWaitAfterwards(Integer timeToWaitAfterwards) {
        this.timeToWaitAfterwards = timeToWaitAfterwards;
    }
}
