package betsy.common.reporting;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents one row in the resulting csv file
 * <p>
 * TEST NAME; ENGINE NAME; GROUP NAME; SUCCESS FLAG; NUMBER OF FAILURES; NUMBER OF TESTS; DEPLOYABLE FLAT
 * <p>
 * FLAG: 1 for successful, 0 for not successful
 */
public class CsvRow implements Comparable<CsvRow> {

    private String name;
    private String engine;
    private String group;
    private String totalFailures;
    private String tests;
    private String deployable;

    public String getBinaryResult() {
        return "0".equals(totalFailures) ? "1" : "0";
    }

    public String toRow() {
        return String.join(";", name, engine, group, getBinaryResult(), totalFailures, tests, deployable);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTotalFailures() {
        return totalFailures;
    }

    public void setTotalFailures(String totalFailures) {
        this.totalFailures = totalFailures;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public String getDeployable() {
        return deployable;
    }

    public void setDeployable(String deployable) {
        this.deployable = deployable;
    }

    @Override
    public int compareTo(CsvRow o) {
        int engineComparison = engine.compareTo(o.engine);
        if (engineComparison != 0) {
            return engineComparison;
        }

        int groupComparison = group.compareTo(o.group);
        if (groupComparison != 0) {
            return groupComparison;
        }

        return name.compareTo(o.name);
    }
}
