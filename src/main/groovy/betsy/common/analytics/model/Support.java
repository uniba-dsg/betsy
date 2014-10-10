package betsy.common.analytics.model;

public enum Support {
    NONE, PARTIAL, TOTAL;

    String toNormalizedSymbol() {
        if (NONE == this) {
            return "-";
        } else if (PARTIAL == this) {
            return "+/-";
        } else {
            return "+";
        }
    }
}
