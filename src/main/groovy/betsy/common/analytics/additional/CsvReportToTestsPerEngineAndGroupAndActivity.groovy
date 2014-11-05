package betsy.common.analytics.additional

import betsy.common.analytics.CsvReportLoader
import betsy.common.analytics.model.CsvReport
import betsy.common.analytics.model.Engine
import betsy.common.analytics.model.Support

import java.nio.file.Paths

class CsvReportToTestsPerEngineAndGroupAndActivity {

    String VALUE_DELIMITER = "\t"
    String ENTRY_DELIMITER = "\n"

    CsvReport report

    Properties properties

    void toCsvReport(PrintStream writer) {

        writer.print "Activity" + VALUE_DELIMITER + report.engines.collect {it.name}.join(VALUE_DELIMITER) + VALUE_DELIMITER + "total" + ENTRY_DELIMITER

        SortedMap<Engine,Integer> successesPerEngineTotal = new TreeMap<>();
        int total = 0

        report.groups.each { group ->
            print group.name + VALUE_DELIMITER * (report.engines.size() + 2) + ENTRY_DELIMITER

            def activityGroups = group.tests.groupBy { getGroupByTestName(it.name) }.sort() {it.key}

            SortedMap<Engine,Integer> successesPerEngine = new TreeMap<>();
            int groupTotal = 0

            activityGroups.each { key, values ->
                writer.print key + VALUE_DELIMITER
                report.engines.each { engine ->
                    int successes = 0
                    values.each { test ->
                        if (test.engineToResult.get(engine).partial == Support.TOTAL) {
                            successes++;
                        }
                    }
                    writer.print successes + VALUE_DELIMITER

                    successesPerEngine.put(engine, successes + successesPerEngine.get(engine, 0))
                    successesPerEngineTotal.put(engine, successes + successesPerEngineTotal.get(engine, 0))
                }
                total += values.size()
                groupTotal += values.size()

                writer.print values.size()
                writer.print ENTRY_DELIMITER
            }
            List<Integer> values = successesPerEngine.collect { key, value -> value}
            print "total" + VALUE_DELIMITER + values.join(VALUE_DELIMITER) + VALUE_DELIMITER + groupTotal + ENTRY_DELIMITER
        }
        List<Integer> values = successesPerEngineTotal.collect { key, value -> value}
        writer.print "total" + VALUE_DELIMITER + values.join(VALUE_DELIMITER) + VALUE_DELIMITER + total + ENTRY_DELIMITER
    }

    private String getGroupByTestName(String testname){
        String group = properties.get(testname)
        if(group == null){
            // Test $testname does not have a group relation. Assuming testname as a group
            group = testname
        }
        return group;
    }

    /**
     *
     * @param args - first elem: Path to CSV, second: properties file to use
     */
    public static void main(String[] args) {
        Properties properties = new Properties()
        properties.load(CsvReportToTestsPerEngineAndGroupAndActivity.class.getResourceAsStream(args[1]))
        new CsvReportToTestsPerEngineAndGroupAndActivity(
                report: new CsvReportLoader(Paths.get(args[0]), new CsvReport()).load(),
                properties: properties
        ).toCsvReport(System.out)
    }

}
