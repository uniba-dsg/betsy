package betsy.bpmn.engines;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pebl.benchmark.test.TestCase;
import pebl.benchmark.test.TestStep;
import pebl.benchmark.test.assertions.AssertTrace;
import pebl.benchmark.test.steps.GatherTraces;
import pebl.benchmark.test.steps.vars.StartProcess;

public class TestCaseUtil {
    public static String getKey(TestCase testCase) {
        return testCase.getTestSteps().stream()
                .filter(ts -> ts instanceof StartProcess)
                .map(ts -> (StartProcess)ts)
                .filter(ts -> !ts.getVariables().isEmpty())
                .map(StartProcess::getProcessName)
                .findFirst().orElseThrow(() -> new IllegalStateException("test case should a key somewhere: " + testCase));
    }

    public static List<String> getTraceAssertions(TestCase testCase) {
        return Optional.of(testCase)
                    .map(tc -> tc.getTestSteps()
                            .stream()
                            .filter(ts -> ts instanceof GatherTraces)
                            .map(ts -> (GatherTraces) ts)
                            .map(TestStep::getTestAssertions)
                            .flatMap(ta -> ta.stream()
                                    .filter(ts -> ts instanceof AssertTrace)
                                    .map(ts -> (AssertTrace) ts)
                                    .map(AssertTrace::getTrace)
                            )
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
