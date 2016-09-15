package pebl.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pebl.test.assertions.Trace;
import pebl.test.assertions.TraceTestAssertion;
import pebl.test.steps.Variable;
import pebl.test.steps.VariableBasedTestStep;

public class VariableBasedTestCase extends TestCase {

    private Integer integerVariable = 0;

    private boolean hasParallelProcess = false;

    public VariableBasedTestCase() {
        this.getTestSteps().add(new VariableBasedTestStep());
    }

    public Optional<Integer> getDelay() {
        return getTestStep().getDelay();
    }

    public boolean hasParallelProcess() {
        return hasParallelProcess;
    }

    public VariableBasedTestStep getTestStep() {
        return (VariableBasedTestStep) Objects.requireNonNull(getTestSteps().get(0), "call input methods before!");
    }

    public List<String> getAssertions() {
        List<TestAssertion> assertions = getTestStep().getAssertions();

        List<String> result = new ArrayList<>();
        for (TestAssertion assertion : assertions) {
            TraceTestAssertion traceTestAssertion = (TraceTestAssertion) assertion;
            result.add(traceTestAssertion.getTrace().toString());
        }
        return result;
    }

    public String getNormalizedTestCaseName() {
        StringBuilder sb = new StringBuilder();
        sb.append("test").append(getNumber()).append("Assert");
        for (String assertion : getAssertions()) {
            sb.append(capitalize(assertion));
        }
        return sb.toString();
    }

    public List<Variable> getVariables() {
        List<Variable> result = new ArrayList<>();

        getTestStep().getVariable().ifPresent(result::add);
        result.add(new Variable("testCaseNumber", "Integer", getNumber()));
        result.add(new Variable("integerVariable", "Integer", integerVariable));

        return result;
    }

    private static String capitalize(String self) {
        if (self == null || self.length() == 0) {
            return self;
        }
        return Character.toUpperCase(self.charAt(0)) + self.substring(1);
    }

    public void setParallelProcess(boolean parallelProcess) {
        this.hasParallelProcess = parallelProcess;
    }
}
