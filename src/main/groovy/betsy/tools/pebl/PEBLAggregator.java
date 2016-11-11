package betsy.tools.pebl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pebl.HasId;
import pebl.benchmark.feature.Metric;
import pebl.benchmark.feature.MetricType;
import pebl.benchmark.feature.ScriptMetricType;
import pebl.result.Measurement;
import pebl.result.Value;
import pebl.result.engine.Engine;
import pebl.result.feature.FeatureResult;
import pebl.result.metrics.BooleanValue;
import pebl.result.metrics.LongValue;
import pebl.result.metrics.StringValue;
import pebl.result.test.TestResult;
import pebl.result.tool.Tool;
import pebl.xsd.PEBL;

public class PEBLAggregator {

    public void computeFeatureResults(PEBL pebl) {
        final Map<String, List<TestResult>> resultsPerEngineTool = pebl.result.testResults.stream().collect(Collectors.groupingBy(t -> t.getEngine().getId() + HasId.SEPARATOR + t.getTool().getId()));

        // everything is recalculated
        pebl.result.featureResults.clear();

        List<Metric> metrics = new LinkedList<>();
        pebl.benchmark.capabilities.forEach(c -> {
            metrics.addAll(c.getMetrics());
            c.getLanguages().forEach(l -> {
                metrics.addAll(l.getMetrics());
                l.getGroups().forEach(g -> {
                    metrics.addAll(g.getMetrics());
                    g.getFeatureSets().forEach(fs -> {
                        metrics.addAll(fs.getMetrics());
                        fs.getFeatures().forEach(f -> {
                            metrics.addAll(f.getMetrics());
                        });
                    });
                });
            });
        });

        for (Metric metric : metrics) {
            final MetricType metricType = metric.getMetricType();
            if (!(metricType instanceof ScriptMetricType)) {
                throw new IllegalStateException("Metric type must be a script metric type");
            }
            ScriptMetricType scriptMetricType = (ScriptMetricType) metricType;

            for (Map.Entry<String, List<TestResult>> entry : resultsPerEngineTool.entrySet()) {

                if (entry.getValue().isEmpty()) {
                    continue;
                }

                // ensure that only the test results relevant for the metric are there
                List<TestResult> testResults = entry.getValue()
                        .stream()
                        .filter(tr -> {
                            final String postfix = HasId.SEPARATOR + metric.getMetricType().getId();
                            final String featureTreeIdMetricReferences = metric.getId().replace(postfix, "");
                            return tr.getTest().getId().startsWith(featureTreeIdMetricReferences);
                        })
                        .collect(Collectors.toList());

                if (testResults.isEmpty()) {
                    continue;
                }

                System.out.println("Computing " + metric.getId() + " for " + entry.getKey());

                Value value = computeThroughScript(scriptMetricType, testResults);

                Engine engine = testResults.get(0).getEngine();
                Tool tool = testResults.get(0).getTool();
                Measurement measurement = new Measurement(metric, value);
                pebl.result.featureResults.add(new FeatureResult(measurement, engine, tool));
            }
        }
    }

    private Value computeThroughScript(ScriptMetricType scriptMetricType, List<TestResult> testResults) {
        if (scriptMetricType.getId().equals("testCasesSum")) {
            return new LongValue(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCases"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .map(v -> (LongValue) v)
                            .orElse(new LongValue(0))
                    )
                    .mapToLong(LongValue::getValue)
                    .sum()
            );
        } else if (scriptMetricType.getId().equals("testCaseSuccessesSum")) {
            return new LongValue(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseSuccesses"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .map(v -> (LongValue) v)
                            .orElse(new LongValue(0))
                    )
                    .mapToLong(LongValue::getValue)
                    .sum()
            );
        } else if (scriptMetricType.getId().equals("testCaseFailuresSum")) {
            return new LongValue(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseFailures"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .map(v -> (LongValue) v)
                            .orElse(new LongValue(0))
                    )
                    .mapToLong(LongValue::getValue)
                    .sum()
            );
        } else if (scriptMetricType.getId().equals("testSuccessfulCount")) {
            return new LongValue(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testSuccessful"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .map(v -> (BooleanValue) v)
                            .orElse(new BooleanValue(false))
                    )
                    .map(BooleanValue::isValue)
                    .filter(x -> x)
                    .count()
            );
        } else if (scriptMetricType.getId().equals("testDeployableCount")) {
            return new LongValue(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testDeployable"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .map(v -> (BooleanValue) v)
                            .orElse(new BooleanValue(false))
                    )
                    .map(BooleanValue::isValue)
                    .filter(x -> x)
                    .count()
            );
        } else if (scriptMetricType.getId().equals("testResultTrivalentAggregation")) {
            final LongValue testCaseSuccesses = new LongValue(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseSuccesses"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .map(v -> (LongValue) v)
                            .orElse(new LongValue(0))
                    )
                    .mapToLong(LongValue::getValue)
                    .sum()
            );
            final LongValue testCasesFailures = new LongValue(testResults
                    .stream()
                    .map(tr -> tr.getMeasurements()
                            .stream()
                            .filter(m -> m.getMetric().getMetricType().getId().equals("testCaseFailures"))
                            .findFirst()
                            .map(Measurement::getValue)
                            .map(v -> (LongValue) v)
                            .orElse(new LongValue(0))
                    )
                    .mapToLong(LongValue::getValue)
                    .sum()
            );

            if (testCasesFailures.getValue() == 0) {
                return new StringValue("+");
            } else if (testCaseSuccesses.getValue() == 0) {
                return new StringValue("-");
            } else {
                return new StringValue("+/-");
            }
        } else if (scriptMetricType.getId().equals("testsCount")) {
            return new LongValue(testResults
                    .stream()
                    .count()
            );
        }

        throw new IllegalStateException("Cannot compute metric " + scriptMetricType);
    }

}
