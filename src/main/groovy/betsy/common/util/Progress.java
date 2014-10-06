package betsy.common.util;

/**
 * Tracks the progress by means of x/y, e.g., 2/10.
 */
public class Progress {

    private final int max;
    private int current = 0;

    public Progress(int max) {
        this.max = max;
    }

    public void next() {
        current++;
    }

    /**
     * @return "current/max"
     */
    public String toString() {
        return "" + current + "/" + max;
    }

}
