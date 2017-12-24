package betsy.common.analytics.model;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import betsy.common.analytics.aggregation.AggregationRules;
import betsy.common.analytics.aggregation.TrivalentResult;

public class Test implements Comparable<Test> {
    public String getFullName() {
        return name;
    }

    public TrivalentResult getSupport() {
        return AggregationRules.EXTREMA.aggregate(engineToResult.values().stream().map(Result::getSupport).collect(Collectors.toList()));
    }

    public SortedMap<Engine, Result> getEngineToResult() {
        return engineToResult;
    }

    @Override
    public int compareTo(Test o) {
        return getFullName().compareTo(o.getFullName());
    }

    @Override
    public String toString() {
        return "Test{" + "name='" + name + "\'" + ", engineToResult=" + engineToResult + "}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEngineToResult(SortedMap<Engine, Result> engineToResult) {
        this.engineToResult = engineToResult;
    }

    private String name;
    private SortedMap<Engine, Result> engineToResult = new TreeMap<>();
}
