package betsy.executables.analytics.model


class Result {

    static enum Support {
        NONE, PARTIAL, TOTAL

        String toNormalizedSymbol() {
            if (NONE == this) {
                "-"
            } else if (PARTIAL == this) {
                "+/-"
            } else {
                "+"
            }
        }
    }

    Integer failed
    Integer total

    Support getPartial() {
        if (failed == 0) {
            Support.TOTAL
        } else if (failed < total) {
            Support.PARTIAL
        } else {
            Support.NONE
        }
    }

    boolean isSuccessful() {
        return failed == 0
    }

    @Override
    public String toString() {
        return "Result{" +
                "failed=" + failed +
                ", total=" + total +
                '}';
    }
}
