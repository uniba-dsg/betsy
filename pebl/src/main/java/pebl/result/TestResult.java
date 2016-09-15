package pebl.result;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import pebl.engine.Engine;
import pebl.test.Test;
import pebl.tool.Tool;

public class TestResult {

    private final Test test;
    private final Engine engine;
    private final Tool tool;

    private final long executionTimestamp;

    private final List<Path> logFiles;
    private final List<Path> deploymentPackage;
    private final List<Path> files;

    private final long executionDuration;
    private final NamedMetrics metrics;
    private final Map<String, String> additionalInformation;
    private final List<TestCaseResult> testCaseResults;

    public TestResult(Test test,
            Engine engine,
            Tool tool,
            long executionTimestamp,
            List<Path> logFiles,
            List<Path> deploymentPackage,
            List<Path> files,
            long executionDuration,
            NamedMetrics metrics,
            Map<String, String> additionalInformation,
            List<TestCaseResult> testCaseResults) {
        this.test = test;
        this.engine = engine;
        this.tool = tool;
        this.executionTimestamp = executionTimestamp;
        this.logFiles = logFiles;
        this.deploymentPackage = deploymentPackage;
        this.files = files;
        this.executionDuration = executionDuration;
        this.metrics = metrics;
        this.additionalInformation = additionalInformation;
        this.testCaseResults = testCaseResults;
    }
}
