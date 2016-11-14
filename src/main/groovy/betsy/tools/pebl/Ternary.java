package betsy.tools.pebl;

import java.util.Arrays;

public enum Ternary {

    PLUS(2, "+"),
    PLUS_MINUS(1, "+/-"),
    MINUS(0, "-");

    private final int number;
    private final String string;

    Ternary(int number, String string) {
        this.number = number;
        this.string = string;
    }

    public int getNumber() {
        return number;
    }

    public String getString() {
        return string;
    }

    public static Ternary from(int number) {
        return Arrays.stream(Ternary.values()).filter(t -> t.getNumber() == number).findFirst().orElseThrow(() -> new IllegalArgumentException("Number " + number + " not available"));
    }

    public static Ternary from(String string) {
        return Arrays.stream(Ternary.values()).filter(t -> t.getString().equals(string)).findFirst().orElseThrow(() -> new IllegalArgumentException("String " + string + " not available"));
    }
}
