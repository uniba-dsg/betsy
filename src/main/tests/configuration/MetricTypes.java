package configuration;

import java.util.LinkedList;
import java.util.List;

import pebl.benchmark.feature.MetricType;
import pebl.benchmark.feature.ValueType;

public class MetricTypes {

    static final MetricType PROCESS_DURATION = new MetricType(ValueType.STRING, "process_duration", "The duration of the process instances.", "seconds");
    static final MetricType THROUGHPUT = new MetricType(ValueType.STRING, "throughput", "The number of process instances completed per second.", "process instances/second");
    static final MetricType NUMBER_OF_PROCESS_INSTANCES = new MetricType(ValueType.STRING, "number_of_process_instances", "The number of process instances.", "quantity");
    static final MetricType CPU = new MetricType(ValueType.STRING, "cpu", "The CPU utilization in percentage.", "%");
    static final MetricType RAM = new MetricType(ValueType.STRING, "ram", "The RAM utilization in MegaByte.", "MB");
    static final MetricType SIZE_OF_STORED_DATA = new MetricType(ValueType.STRING, "size_of_stored_data", "The size of data stored in the DBMS in MegaByte.", "MB");
    static final MetricType IO = new MetricType(ValueType.STRING, "io", "The IO utilization in MegaByte.", "MB");
    static final MetricType EXECUTION_DURATION = new MetricType(ValueType.LONG, "executionDuration", "The time the execution of this particular test.", "milliseconds");
    static final MetricType EXECUTION_TIMESTAMP = new MetricType(ValueType.LONG, "executionTimestamp", "The time the execution of this particular test has been started.", "timestamp");
    static final MetricType TEST_CASES = new MetricType(ValueType.LONG, "testCases", "The number of test cases.", "quantity");
    static final MetricType TEST_CASES_SUCCESSFUL = new MetricType(ValueType.LONG, "testCaseSuccesses", "The number of successful test cases.", "quantity");
    static final MetricType TEST_CASES_FAILURE = new MetricType(ValueType.LONG, "testCaseFailures", "The number of failed test cases.", "quantity");
    static final MetricType TEST_DEPLOYABLE = new MetricType(ValueType.BOOLEAN, "testDeployable", "Whether the underlying process model was deployed successfully.", "");
    static final MetricType TEST_SUCCESSFUL = new MetricType(ValueType.BOOLEAN, "testSuccessful", "Whether all test cases were successfully.", "");
    static final MetricType TEST_RESULT = new MetricType(ValueType.STRING, "testResult", "The overall result based on the number of successful test cases compared to the total number of test cases", "");

    public static final MetricType TESTS_COUNT = new MetricType(ValueType.LONG, "testsCount", "Sum of the test cases", "sum", "SUM(tests)");
    public static final MetricType TEST_CASES_SUM = new MetricType(ValueType.LONG, "testCasesSum", "Sum of the test cases", "sum", "SUM(testCases)");
    public static final MetricType TEST_CASES_SUCCESSFUL_SUM = new MetricType(ValueType.LONG, "testCaseSuccessesSum", "Sum of the test cases", "sum", "SUM(testCaseSuccesses)");
    public static final MetricType TEST_CASES_FAILURE_SUM = new MetricType(ValueType.LONG, "testCaseFailuresSum", "Sum of the test cases", "sum", "SUM(testCaseFailures)");
    public static final MetricType TEST_DEPLOYABLE_COUNT = new MetricType(ValueType.LONG, "testDeployableCount", "Sum of the test cases", "count", "COUNT(testDeployable)");
    public static final MetricType TEST_SUCCESSFUL_COUNT = new MetricType(ValueType.LONG, "testSuccessfulCount", "Sum of the test cases", "count", "COUNT(testSuccessful)");
    public static final MetricType TEST_RESULT_TRIVALENT_AGGREGATION = new MetricType(ValueType.STRING, "testResultTrivalentAggregation", "Sum of the test cases", "trivalent", "TRIVALENT_AGGREGATION(testResult)");
    public static final MetricType PATTERN_SUPPORT = new MetricType(ValueType.STRING, "patternSupport", "Pattern Support", "trivalent", "TRIVALENT_MAX(testResult,languageSupport)");
    public static final MetricType PATTERN_IMPLEMENTATION_SUPPORT = new MetricType(ValueType.STRING, "patternImplementationSupport", "Pattern Implementation Support", "trivalent", "TRIVALENT_MAX(testResult,languageSupport)");

    public static final MetricType PATTERN_IMPLEMENTATION_FULFILLED_LANGUAGE_SUPPORT = new MetricType(ValueType.STRING, "patternImplementationFulfilledLanguageSupport", "patternImplementationFulfilledLanguageSupport", "boolean", "testResult == languageSupport ? + : -");
    public static final MetricType PATTERN_FULFILLED_LANGUAGE_SUPPORT = new MetricType(ValueType.STRING, "patternFulfilledLanguageSupport", "patternFulfilledLanguageSupport", "boolean", "testResult == languageSupport ? + : -");

    public static List<? extends MetricType> getMetricTypes() {
        final LinkedList<MetricType> metricTypes = new LinkedList<>();

        metricTypes.add(PROCESS_DURATION);
        metricTypes.add(THROUGHPUT);
        metricTypes.add(NUMBER_OF_PROCESS_INSTANCES);
        metricTypes.add(CPU);
        metricTypes.add(RAM);
        metricTypes.add(SIZE_OF_STORED_DATA);
        metricTypes.add(IO);
        metricTypes.add(EXECUTION_DURATION);
        metricTypes.add(EXECUTION_TIMESTAMP);

        metricTypes.add(TEST_CASES);
        metricTypes.add(TEST_CASES_SUCCESSFUL);
        metricTypes.add(TEST_CASES_FAILURE);
        metricTypes.add(TEST_DEPLOYABLE);
        metricTypes.add(TEST_SUCCESSFUL);
        metricTypes.add(TEST_RESULT);

        metricTypes.add(TESTS_COUNT);
        metricTypes.add(TEST_CASES_SUM);
        metricTypes.add(TEST_CASES_SUCCESSFUL_SUM);
        metricTypes.add(TEST_CASES_FAILURE_SUM);
        metricTypes.add(TEST_DEPLOYABLE_COUNT);
        metricTypes.add(TEST_SUCCESSFUL_COUNT);
        metricTypes.add(TEST_RESULT_TRIVALENT_AGGREGATION);
        metricTypes.add(PATTERN_SUPPORT);
        metricTypes.add(PATTERN_IMPLEMENTATION_SUPPORT);
        metricTypes.add(PATTERN_IMPLEMENTATION_FULFILLED_LANGUAGE_SUPPORT);
        metricTypes.add(PATTERN_FULFILLED_LANGUAGE_SUPPORT);


        return metricTypes;
    }
}
