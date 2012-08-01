package betsy.executables.reporting.csv

class CsvToReports {

    public static void main(String[] args) {
        CsvToReports csvToLatexTable = new CsvToReports(file: args[0])
        csvToLatexTable.load()
        csvToLatexTable.toHtmlReport("results.html")
        csvToLatexTable.toLatexReport("results.tex")
    }

    String file
    CsvReport report

    void load() {
        report = new CsvReportLoader().loadFromCsv(file)
    }

    void toLatexReport(String filename) {
        new CsvReportToLatexTables(report: report).toLatexReport(filename)
    }

    void toHtmlReport(String filename) {
        new CsvReportToDatawarehouse(report: report).toHtmlReport(filename)
    }

}
