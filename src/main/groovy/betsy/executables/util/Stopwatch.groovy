package betsy.executables.util


class Stopwatch {

    private long start
    private long stop

    static Stopwatch benchmark(Closure closure) {
        Stopwatch stopwatch = new Stopwatch()
        stopwatch.start()
        closure.call()
        stopwatch.stop()

		return stopwatch
    }
	
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
        "${getSecondsDiff()}s"
    }
	
	/**
	* @return raw diff in seconds seconds.
	*/
   public String getSecondsDiff() {
	   diff / 1000
   }

    public String toString() {
        "Duration: $formattedDiff"
    }


}
