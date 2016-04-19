package betsy.common.model.input;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TestPartner {



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

    public static class IntegerOutputBasedOnScriptResult extends Output {

        public final String variable;

        public IntegerOutputBasedOnScriptResult(String variable) {
            this.variable = variable;
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

    public static final TestPartner DUMMY_TEST_PARTNER = new TestPartner(
            Paths.get("TestPartner.wsdl"), "http://localhost:200/bpel-assigned-testpartner");
    public static final TestPartner REGULAR_TEST_PARTNER = new TestPartner(
            Paths.get("TestPartner.wsdl"),
            "http://localhost:200/bpel-testpartner",
            new OperationInputActionOutput("startProcessAsync", new AnyInput()),
            new OperationInputActionOutput("startProcessWithEmptyMessage", new AnyInput()),
            new OperationInputActionOutput("startProcessSync", new IntegerInput(-5), new FaultOutput(FaultOutput.FaultVariant.UNDECLARED)),
            new OperationInputActionOutput("startProcessSync", new IntegerInput(-6), new FaultOutput(FaultOutput.FaultVariant.DECLARED)),
            new OperationInputActionOutput("startProcessSync", new IntegerInput(100), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.access()")),
            new OperationInputActionOutput("startProcessSync", new IntegerInput(101), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.getNumberOfConcurrentCalls()")),
            new OperationInputActionOutput("startProcessSync", new IntegerInput(102), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.getNumberOfCalls()")),
            new OperationInputActionOutput("startProcessSync", new IntegerInput(103), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.reset()")),
            new OperationInputActionOutput("startProcessSync", new AnyInput(), new EchoInputAsOutput())
    );

    public static final TestPartner ERROR_TEST_PARTNER = new TestPartner(
            Paths.get("TestPartner.wsdl"),
            "http://localhost:200/bpel-testpartner",
            new OperationInputActionOutput("startProcessSync", new AnyInput(), new EchoInputAsOutput()));

    private final Path interfaceDescription;
    private final String publishedUrl;

    // operation -> input -> action
    private final List<OperationInputActionOutput> semantics = new LinkedList<>();

    public TestPartner(Path interfaceDescription, String publishedUrl, OperationInputActionOutput... operationInputActionOutputs) {
        this.publishedUrl = publishedUrl;
        this.interfaceDescription = Objects.requireNonNull(interfaceDescription);
        this.semantics.addAll(Arrays.asList(operationInputActionOutputs));
    }

    private static final class OperationInputActionOutput {

        public final String operation;
        public final Input input;
        public final Output output;

        private OperationInputActionOutput(String operation, Input input) {
            this(operation, input, new Output());
        }

        private OperationInputActionOutput(String operation, Input input, Output output) {
            this.output = Objects.requireNonNull(output);
            this.operation = Objects.requireNonNull(operation);
            this.input = Objects.requireNonNull(input);
        }
    }

    public String getWSDLUrl() {
        return publishedUrl + "?wsdl";
    }

}