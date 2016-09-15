package pebl.test.assertions;

import java.util.Objects;

public class Trace {

    private final String value;

    public Trace(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
