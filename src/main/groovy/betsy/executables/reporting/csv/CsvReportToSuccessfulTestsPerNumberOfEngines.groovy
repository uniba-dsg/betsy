package betsy.executables.reporting.csv


class CsvReportToSuccessfulTestsPerNumberOfEngines {

    static class ResultPair {
        String name
        int successful
    }

    CsvReport report

    void toCsvReport(PrintStream writer) {
        List<ResultPair> results = []
        report.tests.each { test ->
            int successful = test.engineToResult.values().count { it.partial == Result.Support.TOTAL}
            results << new ResultPair(name: test.name, successful: successful)
        }

        results.sort { a,b -> (b.successful <=> a.successful) ?: (a.name <=> b.name)}

        results.each { result ->
            writer.println "${result.name}\t${result.successful}"
        }

    }

    public static void main(String[] args) {
        CsvToReports csvToLatexTable = new CsvToReports(file: args[0])
        csvToLatexTable.load()
        new CsvReportToSuccessfulTestsPerNumberOfEngines(report: csvToLatexTable.report).toCsvReport(System.out)
    }
}
