package pebl.builder;

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

    public Ternary aggregate(Ternary b) {
        if(this.equals(b)) {
            return this;
        } else {
            return PLUS_MINUS;
        }
    }

    public Ternary max(Ternary b) {
        if(PLUS.equals(this) || PLUS.equals(b)) {
            return PLUS;
        } else if(PLUS_MINUS.equals(this) || PLUS_MINUS.equals(b)) {
            return PLUS_MINUS;
        } else {
            return MINUS;
        }
    }

    public Ternary atMost(Ternary b) {
        int n = number;
        if(n > b.getNumber()) {
            n = b.getNumber();
        }
        return Ternary.from(n);
    }
}
