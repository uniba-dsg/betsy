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

    public boolean isTotal() {
        return this == TOTAL;
    }

    public boolean isPartial() {
        return this == PARTIAL;
    }

    public boolean isNone() {
        return this == NONE;
    }
}
