package pebl.result;

import java.util.Objects;

public class TestCaseResult {

    private final String name;
    private final int number;
    private final String message;

    public TestCaseResult(String name, int number, String message) {
        this.name = Objects.requireNonNull(name);
        this.number = Objects.requireNonNull(number);
        this.message = Objects.requireNonNull(message);
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }
}
