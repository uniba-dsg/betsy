package betsy.executables.util;

public class Stopwatch {

    private long start;
    private long stop;

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        stop = System.currentTimeMillis();
    }

    public long getDiff() {
        return stop - start;
    }

    /**
     * @return formatted diff using format XXX minutes and YYY.ZZZ seconds.
     */
    public String getFormattedDiff() {
        return getSecondsDiff() + "s";
    }

    /**
     * @return raw diff in seconds seconds.
     */
    public String getSecondsDiff() {
        return "" + (getDiff() / 1000);
    }

    public String toString() {
        return "Duration: " + getFormattedDiff();
    }


}
