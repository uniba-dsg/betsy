package pebl.builder;

import java.util.List;

import pebl.benchmark.feature.Metric;
import pebl.result.test.TestResult;

public interface AggregateScript {

    String aggregate(List<TestResult> testResults);

}
