package betsy.executables.reporting.csv

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
        CsvToReports csvToLatexTable = new CsvToReports(file: args[0])
        csvToLatexTable.load()
        Properties properties = new Properties()
        properties.load(CsvReportToTestsPerEngineAndGroupAndActivity.class.getResourceAsStream("groups.properties"))
        new CsvReportToTestsPerEngineAndGroupAndActivity(
                report: csvToLatexTable.report,
                properties: properties
        ).toCsvReport(System.out)
    }

}
