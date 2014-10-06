package betsy.common.model.steps;

import betsy.common.model.TestStep;

public class DelayTestStep extends TestStep {
    /**
     * Time to wait/delay further test execution after processing this step in milliseconds.
     */
    private Integer timeToWaitAfterwards;

    @Override
    public String toString() {
        return "DelayTestStep{description=" + getDescription() + ", timeToWaitAfterwards=" + String.valueOf(timeToWaitAfterwards) + "}";
    }

    public Integer getTimeToWaitAfterwards() {
        return timeToWaitAfterwards;
    }

    public void setTimeToWaitAfterwards(Integer timeToWaitAfterwards) {
        this.timeToWaitAfterwards = timeToWaitAfterwards;
    }
}
