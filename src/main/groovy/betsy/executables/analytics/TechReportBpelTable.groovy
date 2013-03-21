package betsy.executables.analytics

import betsy.executables.analytics.model.CsvReport


class TechReportBpelTable {

    CsvReport report

    public void toLatexReport(String filename) {
        BpelAnalyzer bpelAnalyzer = new BpelAnalyzer(new File("src/main/tests"), new PrintStream(new File(filename)))
        report.nameToGroup.each { groupName, group ->
            group.tests.each { test ->
                bpelAnalyzer.printLatexTableWithResult("${test.fullName}.bpel", test.engineToResult.collect { name, value -> value.partial.toNormalizedSymbol() }.join(" & "))
            }
        }
    }
}
