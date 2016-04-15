package betsy.common.reporting;

/**
 * Represents one row in the resulting csv file
 * <p>
 * TEST NAME; ENGINE NAME; GROUP NAME; SUCCESS FLAG; NUMBER OF FAILURES; NUMBER OF TESTS; DEPLOYABLE FLAG
 * <p>
 * FLAG: 1 for successful, 0 for not successful
 */
public class CsvRow implements Comparable<CsvRow> {

    private static final String SEPARATOR = ";";

    private final String name;
    private final String engine;
    private final String group;
    private final int failures;
    private final int tests;
    private final boolean deployable;

    public CsvRow(String rawCsvRow) {
        String[] fields = rawCsvRow.split(SEPARATOR);
        name = fields[0];
        engine = fields[1];
        group = fields[2];
        failures = Integer.parseInt(fields[4]);
        tests = Integer.parseInt(fields[5]);
        deployable = fields[6].equals("1");
    }

    public CsvRow(String name, String engine, String group, int failures, int tests, boolean deployable) {
        this.name = name;
        this.engine = engine;
        this.group = group;
        this.failures = failures;
        this.tests = tests;
        this.deployable = deployable;
    }

    public boolean isSuccess() {
        return failures == 0;
    }

    public String toRow() {
        return String.join(SEPARATOR, name, engine, group, valueOf(isSuccess()), String.valueOf(failures), String.valueOf(tests), valueOf(deployable));
    }

    private static String valueOf(boolean value) {
        return value ? "1" : "0";
    }

    public String getName() {
        return name;
    }

    public String getEngine() {
        return engine;
    }

    public String getGroup() {
        return group;
    }

    public int getFailures() {
        return failures;
    }

    public int getTests() {
        return tests;
    }

    public boolean isDeployable() {
        return deployable;
    }

    public int getSuccesses() {
        return getTests() - getFailures();
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
