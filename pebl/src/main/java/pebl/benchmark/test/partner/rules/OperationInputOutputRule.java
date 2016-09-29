package pebl.benchmark.test.partner.rules;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public final class OperationInputOutputRule {

    @XmlElement(required = true)
    private final String operation;

    @XmlElement(required = true)
    private final Input input;

    @XmlElement(required = true)
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


    public String getOperation() {
        return operation;
    }

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }
}
