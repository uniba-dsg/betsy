package betsy.common.tasks;

import org.apache.log4j.Logger;

public class WaitTasks {

    private static final Logger LOGGER = Logger.getLogger(WaitTasks.class);

    /**
     * Sleeps/waits for a specific amount of milliseconds.
     *
     */
    public static void sleep(int milliseconds) {
        if (milliseconds <= 0) {
            LOGGER.info("Did not sleep because value is " + milliseconds + "ms");
            return;
        }

        LOGGER.info("Sleep for " + milliseconds + " ms NOW");
        sleepInternal(milliseconds);
    }

    public static void sleepInternal(int milliseconds) {
        long max = System.currentTimeMillis() + milliseconds;
        while (max > System.currentTimeMillis()) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException ignore) {
            }
        }
    }

}
