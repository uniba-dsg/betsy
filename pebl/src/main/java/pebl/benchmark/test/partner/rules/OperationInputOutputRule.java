package pebl.benchmark.test.partner.rules;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;

@XmlAccessorType(XmlAccessType.NONE)
public final class OperationInputOutputRule {

    @XmlAttribute(required = true)
    private final String operation;

    @XmlElement(required = true)
    @XmlElementRef
    private final AnyInput input;

    @XmlElement(required = true)
    @XmlElementRef
    private final NoOutput output;

    OperationInputOutputRule() {
        this("", new AnyInput());
    }

    public OperationInputOutputRule(String operation, AnyInput input) {
        this(operation, input, new NoOutput());
    }

    public OperationInputOutputRule(String operation, AnyInput input, NoOutput output) {
        this.output = Objects.requireNonNull(output);
        this.operation = Objects.requireNonNull(operation);
        this.input = Objects.requireNonNull(input);
    }

    public String getOperation() {
        return operation;
    }

    public AnyInput getInput() {
        return input;
    }

    public NoOutput getOutput() {
        return output;
    }
}
