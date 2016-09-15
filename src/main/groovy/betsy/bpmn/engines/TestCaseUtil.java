package betsy.bpmn.engines;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pebl.test.TestCase;
import pebl.test.assertions.Trace;
import pebl.test.assertions.TraceTestAssertion;
import pebl.test.steps.AssertableTestStep;
import pebl.test.steps.GatherAndAssertTracesTestStep;
import pebl.test.steps.vars.ProcessStartWithVariablesTestStep;

public class TestCaseUtil {
    public static String getKey(TestCase testCase) {
        return testCase.getTestSteps().stream()
                .filter(ts -> ts instanceof ProcessStartWithVariablesTestStep)
                .map(ts -> (ProcessStartWithVariablesTestStep)ts)
                .filter(ts -> !ts.getVariables().isEmpty())
                .map(ProcessStartWithVariablesTestStep::getProcess)
                .findFirst().orElseThrow(() -> new IllegalStateException("test case should a key somewhere: " + testCase));
    }

    public static List<String> getTraceAssertions(TestCase testCase) {
        return Optional.of(testCase)
                    .map(tc -> tc.getTestSteps()
                            .stream()
                            .filter(ts -> ts instanceof GatherAndAssertTracesTestStep)
                            .map(ts -> (GatherAndAssertTracesTestStep) ts)
                            .map(AssertableTestStep::getAssertions)
                            .flatMap(ta -> ta.stream()
                                    .filter(ts -> ts instanceof TraceTestAssertion)
                                    .map(ts -> (TraceTestAssertion) ts)
                                    .map(TraceTestAssertion::getTrace)
                                    .map(Trace::getValue))
                            .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    public static String getNormalizedTestCaseName(TestCase testCase) {
        StringBuilder sb = new StringBuilder();
        sb.append("test").append(testCase.getNumber()).append("Assert");
        for (String assertion : getTraceAssertions(testCase)) {
            sb.append(capitalize(assertion));
        }
        return sb.toString();
    }

    private static String capitalize(String self) {
        if (self == null || self.length() == 0) {
            return self;
        }
        return Character.toUpperCase(self.charAt(0)) + self.substring(1);
    }
}
