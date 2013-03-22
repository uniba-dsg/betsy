package betsy.executables.analytics.textables

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport


class TechReportBpelTable {

    CsvReport report

    public static void main(String[] args) {
        new TechReportBpelTable(report: new CsvReportLoader(csvFile: "test/reports/results.csv").load()).toLatexReport("test/reports/mytables.tex")
    }

    public void toLatexReport(String filename) {
        BpelAnalyzer bpelAnalyzer = new BpelAnalyzer(output: filename)
        report.nameToGroup.each { groupName, group ->
            group.tests.each { test ->
                bpelAnalyzer.printLatexTableWithResult("${test.fullName}.bpel", test.engineToResult.collect { name, value -> value.partial.toNormalizedSymbol() }.join(" & "))
            }
        }
    }
}
