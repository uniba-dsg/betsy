package betsy.data.steps

import betsy.data.TestStep

class DelayTestStep extends TestStep {
    /**
     * Time to wait/delay further test execution after processing this step in milliseconds.
     */
    Integer timeToWaitAfterwards


    @Override
    public String toString() {
        return "DelayTestStep{description=$description, timeToWaitAfterwards=$timeToWaitAfterwards}"
    }
}
