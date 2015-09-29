package betsy.common.aggregation;

public enum TrivalentResult implements ResultValue{

    PLUS("+"),
    PLUS_MINUS("+/-"),
    MINUS("-");

    private final String sign;

    TrivalentResult(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public boolean isTotal() {
        return this == PLUS;
    }

    public boolean isPartial() {
        return this == PLUS_MINUS;
    }

    public boolean isNone() {
        return this == MINUS;
    }

}
