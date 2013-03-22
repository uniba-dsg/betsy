package betsy.executables.analytics.texresults

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport
import groovy.text.SimpleTemplateEngine

class LatexAnalytics {

    CsvReport report

    public static void main(String[] args) {
        new LatexAnalytics(report: new CsvReportLoader(csvFile: "test/reports/results.csv").load()).toLatexReport("test/reports/myresults.tex")
    }

    void toLatexReport(String filename) {
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(new File("src\\main\\groovy\\betsy\\executables\\analytics\\texresults\\LatexAnalytics.template").text).make([
                "report": report
        ])

        new File(filename).withWriter("UTF-8") { it.write(template.toString()) }
    }

}
