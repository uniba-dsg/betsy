package betsy.executables.analytics.additional

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport
import betsy.executables.analytics.model.Result

class CsvReportToTestsPerEngineAndGroupAndActivity {

    CsvReport report

    Properties properties

    void toCsvReport(PrintStream writer) {

        writer.println "Activity\t" + report.engines.collect {it.name}.join("\t") + "\ttotal tests"

        report.groups.each { group ->
            def activityGroups = group.tests.groupBy { properties.get(it.name) }.sort() {it.key}
            activityGroups.each { key, values ->
                writer.print key + "\t"
                report.engines.each { engine ->
                    int successes = 0
                    values.each { test ->
                        if (test.engineToResult.get(engine).partial == Result.Support.TOTAL) {
                            successes++;
                        }
                    }
                    writer.print successes + "\t"
                }
                writer.print values.size()
                writer.print "\n"
            }
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties()
        properties.load(CsvReportToTestsPerEngineAndGroupAndActivity.class.getResourceAsStream("groups.properties"))
        new CsvReportToTestsPerEngineAndGroupAndActivity(
                report: new CsvReportLoader(csvFile: args[0]).load(),
                properties: properties
        ).toCsvReport(System.out)
    }

}
