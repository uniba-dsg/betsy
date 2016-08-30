package betsy.common.model.input;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class InternalWSDLTestPartner implements WSDLTestPartner {

    @Override
    public String getPublishedURL() {
        return this.publishedUrl;
    }

    public static class Input {

    }

    public static class AnyInput extends Input {

    }

    public static class IntegerInput extends Input {

        public final int value;

        public IntegerInput(int value) {
            this.value = value;
        }
    }

    public static class Output {

    }

    public static class EchoInputAsOutput extends Output {

    }

    public static class IntegerOutput extends Output {

        public final int value;

        public IntegerOutput(int value) {
            this.value = value;
        }
    }

    public static class RawOutput extends Output {

        public final String value;

        public RawOutput(String value) {
            this.value = value;
        }
    }

    public static class IntegerOutputWithStatusCode extends IntegerOutput {

        public final int statusCode;

        public IntegerOutputWithStatusCode(int value, int statusCode) {
            super(value);
            this.statusCode = statusCode;
        }
    }

    public static class IntegerOutputBasedOnScriptResult extends Output {

        public final String script;

        public IntegerOutputBasedOnScriptResult(String script) {
            this.script = script;
        }
    }

    public static class FaultOutput extends Output {

        enum FaultVariant {
            UNDECLARED, DECLARED
        }

        public final FaultVariant variant;

        public FaultOutput(FaultVariant variant) {
            this.variant = variant;
        }
    }

    public static final InternalWSDLTestPartner DUMMY_TEST_PARTNER = new InternalWSDLTestPartner(
            Paths.get("TestPartner.wsdl"), "http://localhost:2000/bpel-assigned-testpartner");
    public static final InternalWSDLTestPartner REGULAR_TEST_PARTNER = new InternalWSDLTestPartner(
            Paths.get("TestPartner.wsdl"),
            "http://localhost:2000/bpel-testpartner",
            new OperationInputOutputRule("startProcessAsync", new AnyInput()),
            new OperationInputOutputRule("startProcessWithEmptyMessage", new AnyInput()),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(-5), new FaultOutput(FaultOutput.FaultVariant.UNDECLARED)),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(-6), new FaultOutput(FaultOutput.FaultVariant.DECLARED)),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(100), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.access()")),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(101), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.getNumberOfConcurrentCalls()")),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(102), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.getNumberOfCalls()")),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(103), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.reset()")),
            new OperationInputOutputRule("startProcessSync", new AnyInput(), new EchoInputAsOutput())
    );

    public final Path interfaceDescription;
    public final String publishedUrl;

    // operation -> input -> action
    private final List<OperationInputOutputRule> rules;

    public InternalWSDLTestPartner(Path interfaceDescription, String publishedUrl, OperationInputOutputRule... operationInputOutputRules) {
        this.publishedUrl = publishedUrl;
        this.interfaceDescription = Objects.requireNonNull(interfaceDescription);
        this.rules = Collections.unmodifiableList(new LinkedList<>(Arrays.asList(operationInputOutputRules)));
    }

    public List<OperationInputOutputRule> getRules() {
        return rules;
    }

    public static final class OperationInputOutputRule {

        public final String operation;
        public final Input input;
        public final Output output;

        public OperationInputOutputRule(String operation, Input input) {
            this(operation, input, new Output());
        }

        public OperationInputOutputRule(String operation, Input input, Output output) {
            this.output = Objects.requireNonNull(output);
            this.operation = Objects.requireNonNull(operation);
            this.input = Objects.requireNonNull(input);
        }
    }

    public static class TimeoutInsteadOfOutput extends Output {

    }
}
