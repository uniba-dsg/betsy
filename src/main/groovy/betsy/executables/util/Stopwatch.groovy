package betsy.executables.util


class Stopwatch {

    private long start
    private long stop

    public void start() {
        start = System.currentTimeMillis()
    }

    public void stop() {
        stop = System.currentTimeMillis()
    }

    public long getDiff() {
        stop - start
    }

    /**
     * @return formatted diff using format XXX minutes and YYY.ZZZ seconds.
     */
    public String getFormattedDiff() {
        long seconds = (diff / 1000);
        long minutes = (seconds / 60);
        long remainingSeconds = seconds - (minutes * 60);
        long remainingSecondsInPercent = (remainingSeconds * 100) / 60

        "(${minutes}.${addLeadingZeros(remainingSecondsInPercent)} min)"
    }

    String addLeadingZeros(long number) {
        if (number < 10) {
            "0$number"
        } else {
            "$number"
        }
    }

    public String toString() {
        "Duration: $formattedDiff"
    }


}
