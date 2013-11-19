package betsy.executables;

public class Progress {

    private int max;
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
