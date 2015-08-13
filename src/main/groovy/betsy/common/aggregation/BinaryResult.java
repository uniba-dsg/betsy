package betsy.common.aggregation;

/**
 * UNUSED
 */
public enum BinaryResult implements ResultValue {

    PLUS("+"), MINUS("+");

    private final String sign;

    BinaryResult(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public boolean toBoolean() {
        return this == PLUS;
    }

}
