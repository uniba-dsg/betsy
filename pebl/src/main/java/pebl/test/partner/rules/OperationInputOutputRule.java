package pebl.test.partner.rules;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public final class OperationInputOutputRule {

    private final String operation;
    private final Input input;
    private final Output output;

    OperationInputOutputRule() {
        this("", new Input());
    }

    public OperationInputOutputRule(String operation, Input input) {
        this(operation, input, new Output());
    }

    public OperationInputOutputRule(String operation, Input input, Output output) {
        this.output = Objects.requireNonNull(output);
        this.operation = Objects.requireNonNull(operation);
        this.input = Objects.requireNonNull(input);
    }

    @XmlElement(required = true)
    public String getOperation() {
        return operation;
    }

    @XmlElement(required = true)
    public Input getInput() {
        return input;
    }

    @XmlElement(required = true)
    public Output getOutput() {
        return output;
    }
}
