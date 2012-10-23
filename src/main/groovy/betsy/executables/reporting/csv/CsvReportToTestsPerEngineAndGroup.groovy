package betsy.executables.reporting.csv


class CsvReportToTestsPerEngineAndGroup {

    CsvReport report

    void toCsvReport(PrintStream writer) {
        report.getEngines().each { engine ->
            int totalSuccessful = 0
            int totalPartial = 0
            int totalFailed = 0
            report.getGroups().each { group ->
                Collection<Result> results = group.getResultsPerEngine(engine)
                int successful = results.count { it.partial == Result.Support.TOTAL}
                totalSuccessful += successful
                int partial = results.count { it.partial == Result.Support.PARTIAL}
                totalPartial += partial
                int failed = results.count { it.partial == Result.Support.NONE}
                totalFailed += failed

                writer.println "$successful\t${partial == 0 ? '' : partial}\t$failed"
            }

            writer.println "$totalSuccessful\t${totalPartial == 0 ? '' : totalPartial}\t$totalFailed"
        }

        writer.println "Engine in %"
        int total = report.tests.size()
        report.getEngines().each { engine ->
            int totalSuccessful = 0
            report.getGroups().each { group ->
                Collection<Result> results = group.getResultsPerEngine(engine)
                totalSuccessful += results.count { it.partial == Result.Support.TOTAL}
            }

            writer.println "${engine.name}\t$totalSuccessful\t${(int)((double) totalSuccessful / total * 100)}%"
        }
    }

    public static void main(String[] args) {
        CsvToReports csvToLatexTable = new CsvToReports(file: args[0])
        csvToLatexTable.load()
        new CsvReportToTestsPerEngineAndGroup(report: csvToLatexTable.report).toCsvReport(System.out)
    }
}
